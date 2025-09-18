package app.Function.DAO;

import app.Instance.DTO.MovieDTO;
import app.Instance.entities.MovieEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class MovieDAO implements IDAO<MovieDTO, MovieEntity, Integer> {

    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<MovieEntity> findEntityById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            MovieEntity movie = em.find(MovieEntity.class, id);
            return Optional.ofNullable(movie);
        } finally {
            em.close();
        }
    }

    @Override
    public List<MovieEntity> findAllEntity() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<MovieEntity> query = em.createQuery("SELECT m FROM MovieEntity m", MovieEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public MovieEntity persist(MovieEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(entity);
                em.getTransaction().commit();
                return entity;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public MovieEntity update(MovieEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                MovieEntity updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(MovieEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                MovieEntity managedMovieEntity = em.find(MovieEntity.class, entity.getId());
                if (managedMovieEntity != null) {
                    em.remove(managedMovieEntity);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void validateDTO(MovieDTO movieDTO) {
        if (movieDTO.title() == null || movieDTO.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be null or empty");
        }
    }

    // Additional query methods
    public List<MovieEntity> findByTitle(String title) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<MovieEntity> query = em.createQuery(
                "SELECT m FROM MovieEntity m WHERE m.title LIKE :title", MovieEntity.class);
            query.setParameter("title", "%" + title + "%");
            return query.getResultList();
        }
    }

    public List<MovieEntity> findByDirectorId(Integer directorId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<MovieEntity> query = em.createQuery(
                "SELECT m FROM MovieEntity m WHERE m.directorEntity.id = :directorId", MovieEntity.class);
            query.setParameter("directorId", directorId);
            return query.getResultList();
        }
    }
}
