package app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Actor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private Integer id;

    @Column(name = "actor_name", nullable = false, length = 255)
    private String name;

    @Column(name = "actor_age", nullable = false)
    private int age;

    // Many-to-Many relationship with Director
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "actor_director",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors = new HashSet<>();

    // Inverse side of Many-to-Many relationship with Movie
    @ManyToMany(mappedBy = "actors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Movie> movies = new HashSet<>();

    // Implementation of BaseEntity interface
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    // Helper methods for bidirectional relationship management with Director
    public void addDirector(Director director) {
        this.directors.add(director);
        director.getActors().add(this);
    }

    public void removeDirector(Director director) {
        this.directors.remove(director);
        director.getActors().remove(this);
    }

    // Helper methods for bidirectional relationship management with Movie
    public void addMovie(Movie movie) {
        this.movies.add(movie);
        movie.getActors().add(this);
    }

    public void removeMovie(Movie movie) {
        this.movies.remove(movie);
        movie.getActors().remove(this);
    }
}
