package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private Long id;

    @Column(name = "actor_name", nullable = false, length = 255)
    private String name;

    @Column(name = "actor_age", nullable = false)
    private int age;

    // Owning side of the Many-to-Many relationship
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "actor_director",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors = new HashSet<>();

    // Helper methods for bidirectional relationship management
    public void addDirector(Director director) {
        this.directors.add(director);
        director.getActors().add(this);
    }

    public void removeDirector(Director director) {
        this.directors.remove(director);
        director.getActors().remove(this);
    }
}
