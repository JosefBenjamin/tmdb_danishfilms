package app.Function.DAO;

import app.Instance.DTO.DirectorDTO;
import app.Instance.entities.DirectorEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DirectorDAO implements IDAO<DirectorDTO, DirectorEntity, Integer> {

    private final EntityManagerFactory emf;

    public DirectorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    @Override
    public Optional<DirectorEntity> findEntityById(Integer integer) {
        EntityManager em = emf.createEntityManager();
        try {
            DirectorEntity director = em.find(DirectorEntity.class, integer);
            return Optional.ofNullable(director);
        } finally {
            em.close();
        }
    }

    @Override
    public List<DirectorEntity> findAllEntity() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DirectorEntity> query = em.createQuery("SELECT d FROM DirectorEntity d", DirectorEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public DirectorEntity persist(DirectorEntity entity) {
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
    public DirectorEntity update(DirectorEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                DirectorEntity updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(DirectorEntity entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                DirectorEntity managedDirectorEntity = em.find(DirectorEntity.class, entity.getId());
                if (managedDirectorEntity != null) {
                    em.remove(managedDirectorEntity);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public void validateDTO(DirectorDTO directorDTO) {

    }
}
