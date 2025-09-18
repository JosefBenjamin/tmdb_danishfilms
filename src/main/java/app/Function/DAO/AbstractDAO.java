package app.Function.DAO;

import app.Instance.DTO.IDTO;
import app.Instance.entities.IEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base DAO providing common database operations
 * @param <DTO> The Data Transfer Object type
 * @param <Entity> The Entity type
 * @param <ID> The ID type
 */
public abstract class AbstractDAO<DTO extends IDTO<ID>, Entity extends IEntity<ID>, ID>
        implements IDAO<DTO, Entity, ID> {

    protected final EntityManagerFactory emf;
    protected final Class<Entity> entityClass;

    public AbstractDAO(EntityManagerFactory emf, Class<Entity> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    @Override
    public Optional<Entity> findEntityById(ID id) {
        try (EntityManager em = emf.createEntityManager()) {
            Entity entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<Entity> findAllEntity() {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<Entity> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        }
    }

    @Override
    public Entity persist(Entity entity) {
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
    public Entity update(Entity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Entity updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(Entity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Entity managedEntity = em.find(entityClass, entity.getId());
                if (managedEntity != null) {
                    em.remove(managedEntity);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void validateDTO(DTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }
    }
}
