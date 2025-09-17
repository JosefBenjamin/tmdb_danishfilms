package app.services;

import java.util.List;

/**
 * Base interface for all Service classes
 * Provides common contract for all service implementations
 * @param <T> the DTO type this service works with
 * @param <ID> the ID type for the entity
 */
public interface Service<T, ID> {
    /**
     * Retrieves all entities as DTOs
     * @return List of all DTOs
     */
    List<T> getAll();

    /**
     * Retrieves an entity by ID as DTO
     * @param id the ID to search for
     * @return the DTO if found
     */
    T getById(ID id);

    /**
     * Saves a new entity
     * @param dto the DTO to persist
     * @return the saved DTO
     */
    T save(T dto);

    /**
     * Updates an existing entity
     * @param dto the DTO to update
     * @return the updated DTO
     */
    T update(T dto);

    /**
     * Deletes an entity by ID
     * @param id the ID of the entity to delete
     */
    void delete(ID id);
}
