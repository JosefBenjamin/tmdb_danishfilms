package app.services;
import java.util.*;
import app.DAO.*;
import app.exceptions.ApiException;
import app.services.*;
import app.DTO.*;
import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


public abstract class AbstractService<DTO, Entity, ID> {
    private final EntityManagerFactory emf;

    public AbstractService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public BaseEntity convertToEntity(DTO dto){
        BaseEntity result = null;

        if (dto.getClass() == (ActorDTO.class) ){
            result = new Actor().builder()
                                    .name(((ActorDTO) dto).name())
                                    .build();
            return result;
        } else if (dto.getClass() == (DirectorDTO.class)){
            result = new Director().builder()
                                    .name(((DirectorDTO) dto).name())
                                    .build();
            return result;
        } else if (dto.getClass() == (MovieDTO.class)){
            result = new Movie().builder()
                                        .title(String.valueOf(dto))
                                        .releaseYear((((MovieDTO) dto).releaseYear()))
                                        .originalLanguage(((MovieDTO) dto).originalLanguage())
                                        .build();
            return result;
        } else if (dto.getClass() == (GenreDTO.class)){
            result = new Genre().builder()
                                        .genreName(((GenreDTO) dto).genreName())
                                        .build();
            return result;
        } else return result;
    }

    public Record convertToDTO(Entity entity)
    {
        Record result = null;
        if (entity.getClass() == Actor.class){
            result = (ActorDTO) new ActorDTO(
                                        ((Actor) entity).getId(),
                                        ((Actor) entity).getName(),
                                        "Acting"
                                            );
                 return result;
        } else if (entity.getClass() == Director.class){
            result = (DirectorDTO) new DirectorDTO(
                                        ((Director) entity).getId(),
                                        ((Director) entity).getName(),
                                        "Directing"
                                            );
                 return result;
        } else if (entity.getClass() == Movie.class){
            result = (MovieDTO) new MovieDTO(
                                        ((Movie) entity).getId(),
                                        ((Movie) entity).getTitle(),
                                        ((Movie) entity).getReleaseYear(),
                                        ((Movie) entity).getOriginalLanguage(),
                                        new HashSet<Integer>()
                                            );
                 return result;

        } else if (entity.getClass() == Genre.class){
            result = (GenreDTO) new GenreDTO(
                                        ((Genre) entity).getId(),
                                        ((Genre) entity).getGenreName()
                                            );
                 return result;

        } else return result;
    }

    public  BaseEntity saveEntity(Entity entity) {
        BaseEntity result = null;
        try {
            if (entity.getClass() == Actor.class){
                try (EntityManager em = emf.createEntityManager()) {
                    em.getTransaction().begin();
                    try {
                        em.persist(entity);
                        em.getTransaction().commit();
                        result = new Actor().builder()
                                            .id(((Actor) entity).getId())
                                            .name(((Actor) entity).getName())
                                            .build();
                        return result;
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        throw e;
                    }
                }

            } else if (entity.getClass() == Director.class){
                try (EntityManager em = emf.createEntityManager()) {
                    em.getTransaction().begin();
                    try {
                        em.persist(entity);
                        em.getTransaction().commit();
                        result = new Director().builder().id(((Director) entity).getId())
                                                .name(((Director) entity).getName())
                                                .build();
                        return result;
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        throw e;
                    }
                }

            } else if (entity.getClass() == Movie.class){
                try (EntityManager em = emf.createEntityManager()) {
                    em.getTransaction().begin();
                    try {
                        em.persist(entity);
                        em.getTransaction().commit();
                        result = new Movie().builder().id(((Movie) entity).getId())
                                                .title(((Movie) entity).getTitle())
                                                .releaseYear(((Movie) entity).getReleaseYear())
                                                .originalLanguage(((Movie) entity).getOriginalLanguage())
                                                .build();
                        return result;
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        throw e;
                    }
                }

            } else if (entity.getClass() == Genre.class){
                try (EntityManager em = emf.createEntityManager()) {
                    em.getTransaction().begin();
                    try {
                        em.persist(entity);
                        em.getTransaction().commit();
                        result = new Genre().builder().id(((Genre) entity).getId())
                                                .genreName(((Genre) entity).getGenreName())
                                                .build();
                        return result;
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        throw e;
                    }
                }
            } else
            return null;
        } catch (Exception e) {
            throw ApiException.serverError("Could not save entity: " + e.getMessage());
        }
    }
}
