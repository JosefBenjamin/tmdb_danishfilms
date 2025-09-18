package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "genres")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"movies"})
public class Genre implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    private String genreName;

    // Inverse side of Many-to-Many relationship with Movie
    @ManyToMany(mappedBy = "genres", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
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

    // Helper methods for bidirectional relationship management with Movie
    public void addMovie(Movie movie) {
        this.movies.add(movie);
        movie.getGenres().add(this);
    }

    public void removeMovie(Movie movie) {
        this.movies.remove(movie);
        movie.getGenres().remove(this);
    }
}
