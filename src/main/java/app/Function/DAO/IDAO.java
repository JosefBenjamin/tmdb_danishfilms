package app.Function.DAO;

import java.util.List;
import java.util.Optional;

public interface IDAO<DTO, Entity, ID> {

    Optional<Entity> findEntityById(ID id);

    List<Entity> findAllEntity();

    Entity persist(Entity entity);

    Entity update(Entity entity);

    void delete(Entity entity);

    void validateDTO(DTO dto);

}

