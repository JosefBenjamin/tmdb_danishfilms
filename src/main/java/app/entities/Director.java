package app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //only uses id for equality
@ToString(exclude = {"actors", "movies"}) // avoids recursion
public class Director implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include  // only id is used
    private Integer id;

    private String name;
    private int age;

    @ManyToMany(mappedBy = "directors")
    @Builder.Default
    private Set<Actor> actors = new HashSet<>();


    @OneToMany(mappedBy = "director")
    @Builder.Default
    private Set<Movie> movies = new HashSet<>();

    // ---- bidirectional helpers ----
    public boolean addActor(Actor actor) {
        if (actor == null) return false;
        boolean a = actors.add(actor);
        boolean b = actor.getDirectors().add(this);
        return a || b;
    }

    public boolean removeActor(Actor actor) {
        if (actor == null) return false;
        boolean a = actors.remove(actor);
        boolean b = actor.getDirectors().remove(this);
        return a || b;
    }

    public void addMovie(Movie movie) {
        if (movie == null || movie.getDirector() == this) return;
        movie.setDirector(this);               // owning side is Movie.director
        if (!movies.contains(movie)) movies.add(movie);
    }

    public void removeMovie(Movie movie) {
        if (movie == null) return;
        if (movies.remove(movie) && movie.getDirector() == this) {
            movie.setDirector(null);
        }
    }

}

