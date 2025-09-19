package app.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"genres", "actors", "director"})
public class Movie implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "tmdbId", unique = true)
    private Integer tmdbId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "release_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Column(name = "original_language")
    private String originalLanguage;

    // Many-to-Many relationship with Genre (Movie can have multiple genres)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "movies_and_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    // Many-to-Many relationship with Actor (Movie can have multiple actors)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "movies_and_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    @Builder.Default
    private Set<Actor> actors = new HashSet<>();

    // Many-to-One relationship with Director (Movie has one director)
    @ManyToOne(fetch = FetchType.EAGER)
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

    // Helper methods for bidirectional add with Genre
    public void addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getMovies().add(this);
    }

    // Helper methods for bidirectional remove with Genre
    public void removeGenre(Genre genre) {
        this.genres.remove(genre);
        genre.getMovies().remove(this);
    }

    // Helper methods for bidirectional add with Actor
    public void addActor(Actor actor) {
        this.actors.add(actor);
        actor.getMovies().add(this);
    }

    // Helper methods for bidirectional remove with Actor
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
