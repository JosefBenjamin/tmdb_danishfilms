package app.services;

import app.exceptions.ApiException;

import java.util.List;

/**
 * Base interface for all Service classes
 * Provides common contract for all service implementations
 * @param <DTO> the DTO type this service works with
 * @param <ID> the ID type for the entity
 */
public interface Service<DTO, ID> {

    /**
     * Retrieves all entities as DTOs
     * @return List of all DTOs
     */
    List<DTO> getAll();

    /**
     * Retrieves an entity by ID as DTO
     * @param id the ID to search for
     * @return the DTO if found
     */
    DTO getById(ID id);

    /**
     * Saves a new entity
     * @param dto the DTO to persist
     * @return the saved DTO
     */
    DTO save(DTO dto);

    /**
     * Updates an existing entity
     * @param dto the DTO to update
     * @return the updated DTO
     */
    DTO update(DTO dto);

    /**
     * Deletes an entity by ID
     * @param id the ID of the entity to delete
     */
    void delete(ID id);

}
