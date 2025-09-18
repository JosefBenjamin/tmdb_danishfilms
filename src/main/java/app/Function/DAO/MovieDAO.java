package app.Function.DAO;

import app.Instance.DTO.MovieDTO;
import app.Instance.entities.MovieEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class MovieDAO extends AbstractDAO<MovieDTO, MovieEntity, Integer> {

    public MovieDAO(EntityManagerFactory emf) {
        super(emf, MovieEntity.class);
    }

    @Override
    public void validateDTO(MovieDTO movieDTO) {
        super.validateDTO(movieDTO);
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
