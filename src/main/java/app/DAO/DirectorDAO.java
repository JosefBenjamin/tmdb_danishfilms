package app.DAO;

import app.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DirectorDAO implements IDAO<Director, Integer> {

    private final EntityManagerFactory emf;

    public DirectorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<Director> findById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Director director = em.find(Director.class, id);
            return Optional.ofNullable(director);
        }
    }

    @Override
    public List<Director> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d", Director.class);
            return query.getResultList();
        }
    }

    @Override
    public Director persist(Director entity) {
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
    public Director update(Director entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Director updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(Director entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Director managedDirector = em.find(Director.class, entity.getId());
                if (managedDirector != null) {
                    em.remove(managedDirector);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }
}
