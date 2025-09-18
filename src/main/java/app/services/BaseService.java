package app.services;

import java.util.List;
import java.util.Optional;

/**
 * Generic Service interface that all service classes implement
 * Provides standard CRUD operations for any DTO type
 */
public interface BaseService<DTO, ID> {

    /**
     * Get all entities as DTOs
     */
    List<DTO> getAll();

    /**
     * Get entity by ID as DTO
     */
    Optional<DTO> getById(ID id);

    /**
     * Save entity from DTO
     */
    DTO save(DTO dto);

    /**
     * Update entity from DTO
     */
    DTO update(DTO dto);

    /**
     * Delete entity by ID
     */
    void delete(ID id);
}
