package app.Instance.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "directors")
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"actorEntities", "movieEntities"}) // avoids recursion
public class DirectorEntity extends ResponseEntity<Integer> {

    @Column(name = "director_name", nullable = false)
    private String name;

    @Column(name = "director_age")
    private int age;

    @Column(name = "job")
    private String job;

    // Many-to-Many relationship with Actor - INVERSE side
    @ManyToMany(mappedBy = "directorEntities")
    @Builder.Default
    private Set<ActorEntity> actorEntities = new HashSet<>();

    // One-to-Many relationship with Movie - INVERSE side
    @OneToMany(mappedBy = "directorEntity")
    @Builder.Default
    private Set<MovieEntity> movieEntities = new HashSet<>();

    // ---- bidirectional helpers ----
    public boolean addActor(ActorEntity actorEntity) {
        if (actorEntity == null) return false;
        boolean a = actorEntities.add(actorEntity);
        boolean b = actorEntity.getDirectorEntities().add(this);
        return a || b;
    }

    public boolean removeActor(ActorEntity actorEntity) {
        if (actorEntity == null) return false;
        boolean a = actorEntities.remove(actorEntity);
        boolean b = actorEntity.getDirectorEntities().remove(this);
        return a || b;
    }

    public void addMovie(MovieEntity movieEntity) {
        if (movieEntity == null || movieEntity.getDirectorEntity() == this) return;
        movieEntity.setDirectorEntity(this);               // owning side is Movie.directorEntity
        if (!movieEntities.contains(movieEntity)) movieEntities.add(movieEntity);
    }

    public void removeMovie(MovieEntity movieEntity) {
        if (movieEntity == null) return;
        if (movieEntities.remove(movieEntity) && movieEntity.getDirectorEntity() == this) {
            movieEntity.setDirectorEntity(null);
        }
    }
}
