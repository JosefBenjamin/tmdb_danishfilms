package app.Function.DAO;

import app.Instance.DTO.ActorDTO;
import app.Instance.entities.ActorEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class ActorDAO implements IDAO<ActorDTO, ActorEntity, Integer> {

    private final EntityManagerFactory emf;

    public ActorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<ActorEntity> findEntityById(Integer integer) {
        EntityManager em = emf.createEntityManager();
        try {
            ActorEntity actor = em.find(ActorEntity.class, integer);
            return Optional.ofNullable(actor);
        } finally {
            em.close();
        }
    }

    @Override
    public List<ActorEntity> findAllEntity() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ActorEntity> query = em.createQuery("SELECT a FROM ActorEntity a", ActorEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public ActorEntity persist(ActorEntity actorEntity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(actorEntity);
            em.getTransaction().commit();
            return actorEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public ActorEntity update(ActorEntity actorEntity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ActorEntity updatedActor = em.merge(actorEntity);
            em.getTransaction().commit();
            return updatedActor;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(ActorEntity actorEntity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ActorEntity toDelete = em.find(ActorEntity.class, actorEntity.getId());
            if (toDelete != null) {
                em.remove(toDelete);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void validateDTO(ActorDTO actorDTO) {
        if (actorDTO.name() == null || actorDTO.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Actor name cannot be null or empty");
        }
    }
}
