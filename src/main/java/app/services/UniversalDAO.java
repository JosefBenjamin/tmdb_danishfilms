package app.services;

import app.DAO.IDAO;
import app.DTO.BaseDTO;
import app.entities.BaseEntity;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Universal DAO that works for ANY entity type with ZERO configuration
 * Uses reflection and caching for maximum performance
 */
public class UniversalDAO<Entity extends BaseEntity<ID>, ID> implements IDAO<Entity, ID> {

    private final EntityManagerFactory emf;
    private final Class<Entity> entityClass;
    private final String entityName;

    // Cache for reflection operations
    private static final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, Method>> methodCache = new ConcurrentHashMap<>();

    public UniversalDAO(EntityManagerFactory emf, Class<Entity> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
        this.entityName = entityClass.getSimpleName();

        // Pre-cache reflection data for performance
        cacheReflectionData();
    }

    private void cacheReflectionData() {
        try {
            // Cache constructor
            constructorCache.putIfAbsent(entityClass, entityClass.getDeclaredConstructor());

            // Cache methods
            Map<String, Method> methods = new HashMap<>();
            for (Method method : entityClass.getMethods()) {
                methods.put(method.getName().toLowerCase(), method);
            }
            methodCache.put(entityClass, methods);
        } catch (Exception e) {
            throw new RuntimeException("Failed to cache reflection data for " + entityName, e);
        }
    }

    @Override
    public Optional<Entity> findById(ID id) {
        try (var em = emf.createEntityManager()) {
            Entity entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<Entity> findAll() {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT e FROM " + entityName + " e", entityClass);
            return query.getResultList();
        }
    }

    @Override
    public Entity persist(Entity entity) {
        try (var em = emf.createEntityManager()) {
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
        try (var em = emf.createEntityManager()) {
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
        try (var em = emf.createEntityManager()) {
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

    /**
     * Dynamic query method - finds by any field
     * Usage: findByField("name", "John")
     */
    public List<Entity> findByField(String fieldName, Object value) {
        try (var em = emf.createEntityManager()) {
            String jpql = "SELECT e FROM " + entityName + " e WHERE e." + fieldName + " = :value";
            var query = em.createQuery(jpql, entityClass);
            query.setParameter("value", value);
            return query.getResultList();
        }
    }

    /**
     * Dynamic query method - finds by multiple fields
     * Usage: findByFields(Map.of("name", "John", "age", 30))
     */
    public List<Entity> findByFields(Map<String, Object> criteria) {
        try (var em = emf.createEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT e FROM " + entityName + " e WHERE ");

            List<String> conditions = new ArrayList<>();
            for (String field : criteria.keySet()) {
                conditions.add("e." + field + " = :" + field);
            }

            jpql.append(String.join(" AND ", conditions));

            var query = em.createQuery(jpql.toString(), entityClass);
            for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        }
    }
}
