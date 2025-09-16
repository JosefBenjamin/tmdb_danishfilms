package app.entities;

import jakarta.persistence.*;
import lombok.*;



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

    @OneToMany(mappedBy = "director")
    private List<Movie> movies = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Optional<Director> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Director> findAll() {
        return List.of();
    }

    @Override
    public Director save(Director entity) {
        return null;

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
