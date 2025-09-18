package app.Function.DAO;

import app.Instance.DTO.ActorDTO;
import app.Instance.entities.ActorEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class ActorDAO extends AbstractDAO<ActorDTO, ActorEntity, Integer> {

    public ActorDAO(EntityManagerFactory emf) {
        super(emf, ActorEntity.class);
    }

    @Override
    public void validateDTO(ActorDTO actorDTO) {
        super.validateDTO(actorDTO);
        if (actorDTO.name() == null || actorDTO.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Actor name cannot be null or empty");
        }
    }

    /**
     * Find actor by ID with movies eagerly loaded using JOIN FETCH
     */
    public Optional<ActorEntity> findByIdWithMovies(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ActorEntity> query = em.createQuery(
                "SELECT DISTINCT a FROM ActorEntity a LEFT JOIN FETCH a.movieEntities WHERE a.id = :id", 
                ActorEntity.class);
            query.setParameter("id", id);
            return query.getResultList().stream().findFirst();
        }
    }

    /**
     * Find actor by ID with directors eagerly loaded using JOIN FETCH
     */
    public Optional<ActorEntity> findByIdWithDirectors(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ActorEntity> query = em.createQuery(
                "SELECT DISTINCT a FROM ActorEntity a LEFT JOIN FETCH a.directorEntities WHERE a.id = :id", 
                ActorEntity.class);
            query.setParameter("id", id);
            return query.getResultList().stream().findFirst();
        }
    }

    /**
     * Find actor by ID with both movies and directors eagerly loaded
     */
    public Optional<ActorEntity> findByIdWithAllRelations(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            // First fetch with movies
            TypedQuery<ActorEntity> movieQuery = em.createQuery(
                "SELECT DISTINCT a FROM ActorEntity a LEFT JOIN FETCH a.movieEntities WHERE a.id = :id", 
                ActorEntity.class);
            movieQuery.setParameter("id", id);
            
            Optional<ActorEntity> actorWithMovies = movieQuery.getResultList().stream().findFirst();
            
            if (actorWithMovies.isPresent()) {
                // Then fetch directors for the same actor
                TypedQuery<ActorEntity> directorQuery = em.createQuery(
                    "SELECT DISTINCT a FROM ActorEntity a LEFT JOIN FETCH a.directorEntities WHERE a.id = :id", 
                    ActorEntity.class);
                directorQuery.setParameter("id", id);
                directorQuery.getResultList(); // This will populate the directors collection
                
                return actorWithMovies;
            }
            
            return Optional.empty();
        }
    }
}
