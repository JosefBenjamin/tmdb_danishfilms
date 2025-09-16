package app.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
public class Movie implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private Integer releaseYear;
    private String originalLanguage;

    // Many-to-Many relationship with Genre (Movie can have multiple genres)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "movies_and_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();

    // Many-to-Many relationship with Actor (Movie can have multiple actors)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "movies_and_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private Set<Actor> actors = new HashSet<>();

    // Many-to-One relationship with Director (Movie has one director)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "director_id")
    private Director director;

    // Implementation of BaseEntity interface
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    // Helper methods for bidirectional relationship management with Genre
    public void addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getMovies().add(this);
    }

    public void removeGenre(Genre genre) {
        this.genres.remove(genre);
        genre.getMovies().remove(this);
    }

    // Helper methods for bidirectional relationship management with Actor
    public void addActor(Actor actor) {
        this.actors.add(actor);
        actor.getMovies().add(this);
    }

    public void removeActor(Actor actor) {
        this.actors.remove(actor);
        actor.getMovies().remove(this);
    }

    // Helper methods for bidirectional relationship management with Director
    public void setDirector(Director director) {
        // Remove from previous director if exists
        if (this.director != null) {
            this.director.getMovies().remove(this);
        }

        this.director = director;

        // Add to new director if not null
        if (director != null) {
            director.getMovies().add(this);
        }
    }
}
