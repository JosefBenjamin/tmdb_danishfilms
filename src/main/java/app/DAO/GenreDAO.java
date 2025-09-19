package app.DAO;

import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class GenreDAO implements BaseDAO<Genre, Integer> {

    private final EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Genre genre = em.find(Genre.class, id);
            return Optional.ofNullable(genre);
        }
    }

    public Optional<Genre> findByTmdbId(Integer tmdbId) {
        try (EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT g FROM Genre g WHERE g.tmdbId = :tmdbId", Genre.class)
                    .setParameter("tmdbId", tmdbId)
                    .getResultStream()
                    .findFirst();
        }
    }

    @Override
    public List<Genre> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);
            return query.getResultList();
        }
    }

    @Override
    public Genre persist(Genre entity) {
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
    public Genre update(Genre entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Genre updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(Genre entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Genre managedGenre = em.find(Genre.class, entity.getId());
                if (managedGenre != null) {
                    em.remove(managedGenre);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    // Additional query methods
    public Optional<Genre> findByGenreName(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery(
                "SELECT g FROM Genre g WHERE g.genreName = :genreName", Genre.class);
            query.setParameter("genreName", genreName);
            return query.getResultList().stream().findFirst();
        }
    }

    public List<Genre> findByGenreNameContaining(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery(
                "SELECT g FROM Genre g WHERE g.genreName LIKE :genreName", Genre.class);
            query.setParameter("genreName", "%" + genreName + "%");
            return query.getResultList();
        }
    }
}
