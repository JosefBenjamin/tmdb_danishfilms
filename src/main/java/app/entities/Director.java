package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "directors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Director implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Integer id;

    @Column(name = "director_name", nullable = false, length = 255)
    private String name;

    @Column(name = "director_age", nullable = false)
    private int age;

    // Inverse side of the Many-to-Many relationship with Actor
    @ManyToMany(mappedBy = "directors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Actor> actors = new HashSet<>();

    // One-to-Many relationship with Movie (Director can direct multiple movies)
    @OneToMany(mappedBy = "director", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Movie> movies = new ArrayList<>();

    // Implementation of BaseEntity interface
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    // Helper methods for bidirectional relationship management with Actor
    public void addActor(Actor actor) {
        this.actors.add(actor);
        actor.getDirectors().add(this);
    }

    public void removeActor(Actor actor) {
        this.actors.remove(actor);
        actor.getDirectors().remove(this);
    }

    // Helper methods for bidirectional relationship management with Movie
    public void addMovie(Movie movie) {
        this.movies.add(movie);
        movie.setDirector(this);
    }

    public void removeMovie(Movie movie) {
        this.movies.remove(movie);
        movie.setDirector(null);
    }
}
