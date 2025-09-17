package app.Object.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"genreEntities", "actorEntities", "directorEntity"})
public class MovieEntity implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String title;
    private int releaseYear;
    private String originalLanguage;

    // Many-to-Many relationship with Genre (Movie can have multiple genres)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "movies_and_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @Builder.Default
    private Set<GenreEntity> genreEntities = new HashSet<>();

    // Many-to-Many relationship with Actor (Movie can have multiple actors)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "movies_and_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    @Builder.Default
    private Set<ActorEntity> actorEntities = new HashSet<>();

    // Many-to-One relationship with Director (Movie has one director)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "director_id")
    private DirectorEntity directorEntity;

    // Helper methods for bidirectional relationship management with Genre
    public void addGenre(GenreEntity genreEntity) {
        this.genreEntities.add(genreEntity);
        genreEntity.getMovieEntities().add(this);
    }

    public void removeGenre(GenreEntity genreEntity) {
        this.genreEntities.remove(genreEntity);
        genreEntity.getMovieEntities().remove(this);
    }

    // Helper methods for bidirectional relationship management with Actor
    public void addActor(ActorEntity actorEntity) {
        this.actorEntities.add(actorEntity);
        actorEntity.getMovieEntities().add(this);
    }

    public void removeActor(ActorEntity actorEntity) {
        this.actorEntities.remove(actorEntity);
        actorEntity.getMovieEntities().remove(this);
    }

    // Helper methods for bidirectional relationship management with Director
    public void setDirectorEntity(DirectorEntity directorEntity) {
        // Remove from previous director if exists
        if (this.directorEntity != null) {
            this.directorEntity.getMovieEntities().remove(this);
        }
        this.directorEntity = directorEntity;

        // Add to new director if not null
        if (directorEntity != null) {
            directorEntity.getMovieEntities().add(this);
        }
    }

}
