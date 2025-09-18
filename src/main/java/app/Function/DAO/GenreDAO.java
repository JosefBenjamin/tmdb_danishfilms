package app.Function.DAO;

import app.Instance.DTO.GenreDTO;
import app.Instance.entities.GenreEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class GenreDAO implements IDAO<GenreDTO, GenreEntity, Integer> {

    private final EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    @Override
    public Optional<GenreEntity> findEntityById(Integer integer) {
        EntityManager em = emf.createEntityManager();
        try {
            GenreEntity genre = em.find(GenreEntity.class, integer);
            return Optional.ofNullable(genre);
        } finally {
            em.close();
        }
    }

    @Override
    public List<GenreEntity> findAllEntity() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<GenreEntity> query = em.createQuery("SELECT g FROM GenreEntity g", GenreEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public GenreEntity persist(GenreEntity entity) {
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
    public GenreEntity update(GenreEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                GenreEntity updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(GenreEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                GenreEntity managedGenreEntity = em.find(GenreEntity.class, entity.getId());
                if (managedGenreEntity != null) {
                    em.remove(managedGenreEntity);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void validateDTO(GenreDTO genreDTO) {

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
