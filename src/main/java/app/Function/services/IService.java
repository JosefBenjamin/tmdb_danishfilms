package app.Function.services;

import java.util.List;
import java.util.Optional;

/**
 * Generic Service interface that all service classes implement
 * Provides standard CRUD operations for any DTO type
 */
public interface IService<DTO, Entity ,ID> {

    //  Get all entities as DTOs
    List<DTO> getAll();

 //     Get entity by ID as DTO
     Optional<DTO> getDTOById(ID id);

   //   Get entity by ID
     Optional<Entity> getEntityById(ID id);

   //   Delete entity by ID
    void delete(ID id);

}
