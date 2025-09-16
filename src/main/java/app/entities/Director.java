package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Long id;

    @Column(name = "director_name", nullable = false, length = 255)
    private String name;

    @Column(name = "director_age", nullable = false)
    private int age;

    // Inverse side of the Many-to-Many relationship
    @ManyToMany(mappedBy = "directors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Actor> actors = new HashSet<>();

    // Helper methods for bidirectional relationship management
    public void addActor(Actor actor) {
        this.actors.add(actor);
        actor.getDirectors().add(this);
    }

    public void removeActor(Actor actor) {
        this.actors.remove(actor);
        actor.getDirectors().remove(this);
    }
}
