package app.Function.DAO;

import app.Instance.entities.ActorEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class ActorDAO implements IDAO<ActorEntity, Integer> {

    private final EntityManagerFactory emf;

    public ActorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<ActorEntity> findById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            ActorEntity actorEntity = em.find(ActorEntity.class, id);
            return Optional.ofNullable(actorEntity);
        }
    }

    @Override
    public List<ActorEntity> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ActorEntity> query = em.createQuery("SELECT a FROM ActorEntity a", ActorEntity.class);
            return query.getResultList();
        }
    }


    public ActorEntity persist(ActorEntity entity) {
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
    public ActorEntity update(ActorEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                ActorEntity updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(ActorEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                ActorEntity managedActorEntity = em.find(ActorEntity.class, entity.getId());
                if (managedActorEntity != null) {
                    em.remove(managedActorEntity);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }
}
