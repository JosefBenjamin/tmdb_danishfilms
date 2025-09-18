package app.Function.DAO;

import app.Instance.DTO.GenreDTO;
import app.Instance.entities.GenreEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class GenreDAO extends AbstractDAO<GenreDTO, GenreEntity, Integer> {

    public GenreDAO(EntityManagerFactory emf) {
        super(emf, GenreEntity.class);
    }

    @Override
    public void validateDTO(GenreDTO genreDTO) {
        super.validateDTO(genreDTO);
        if (genreDTO.genreName() == null || genreDTO.genreName().trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be null or empty");
        }
    }

    // Additional query methods
    public Optional<GenreEntity> findByGenreName(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GenreEntity> query = em.createQuery(
                "SELECT g FROM GenreEntity g WHERE g.genreName = :genreName", GenreEntity.class);
            query.setParameter("genreName", genreName);
            return query.getResultList().stream().findFirst();
        }
    }

    public List<GenreEntity> findByGenreNameContaining(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GenreEntity> query = em.createQuery(
                "SELECT g FROM GenreEntity g WHERE g.genreName LIKE :genreName", GenreEntity.class);
            query.setParameter("genreName", "%" + genreName + "%");
            return query.getResultList();
        }
    }
}
