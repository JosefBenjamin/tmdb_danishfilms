package app.entities;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Entity;

import java.util.HashSet;
import java.util.Set;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "directors")
public class Director {
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
}

