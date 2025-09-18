package app.Instance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@SuperBuilder
@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name = "genres")
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"movieEntities"})
public class GenreEntity extends ResponseEntity<Integer> {

    private String genreName;

    // Inverse side of Many-to-Many relationship with Movie
    @ManyToMany(mappedBy = "genreEntities", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private Set<MovieEntity> movieEntities = new HashSet<>();

    // Helper methods for bidirectional relationship management with Movie
    public void addMovie(MovieEntity movieEntity) {
        this.movieEntities.add(movieEntity);
        movieEntity.getGenreEntities().add(this);
    }

    public void removeMovie(MovieEntity movieEntity) {
        this.movieEntities.remove(movieEntity);
        movieEntity.getGenreEntities().remove(this);
    }
}
