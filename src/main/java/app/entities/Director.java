package app.entities;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "directors")
public class Director implements app.DAO.DAO<Director, Long> {
    @Column(name = "director_name", nullable = false, length = 255)
    private String name;

    @Column(name = "director_age", nullable = false)
    private int age;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Long id;

    @ManyToMany(mappedBy = "directors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Actor> actors = new HashSet<>();

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
    }

    @Override
    public Director update(Director entity) {
        return null;
    }

    @Override
    public void delete(Director entity) {

    }
}

