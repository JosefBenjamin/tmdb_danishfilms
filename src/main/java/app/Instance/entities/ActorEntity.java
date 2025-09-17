package app.Instance.entities;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"directorEntities", "movieEntities"})
public class ActorEntity implements IEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    @EqualsAndHashCode.Include
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
    @Builder.Default
    private Set<DirectorEntity> directorEntities = new HashSet<>();

    // Inverse side of Many-to-Many relationship with Movie
    @ManyToMany(mappedBy = "actorEntities", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private Set<MovieEntity> movieEntities = new HashSet<>();


    // Helper methods for bidirectional relationship management with Director
    public void addDirector(DirectorEntity directorEntity) {
        this.directorEntities.add(directorEntity);
        directorEntity.getActorEntities().add(this);
    }

    public void removeDirector(DirectorEntity directorEntity) {
        this.directorEntities.remove(directorEntity);
        directorEntity.getActorEntities().remove(this);
    }

    // Helper methods for bidirectional relationship management with Movie
    public void addMovie(MovieEntity movieEntity) {
        this.movieEntities.add(movieEntity);
        movieEntity.getActorEntities().add(this);
    }

    public void removeMovie(MovieEntity movieEntity) {
        this.movieEntities.remove(movieEntity);
        movieEntity.getActorEntities().remove(this);
    }
}
