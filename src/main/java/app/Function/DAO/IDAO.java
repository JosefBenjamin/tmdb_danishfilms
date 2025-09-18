package app.Function.DAO;

import app.Instance.DTO.GenreDTO;

import java.util.List;
import java.util.Optional;

public interface IDAO<DTO, Entity, ID> {

    Optional<DTO> findDTOById(ID id);

    Optional<Entity> findEntityById(ID id);

    List<DTO> findAllDTO();

    List<Entity> findAllEntity();

    DTO persist(DTO entity);

    Entity update(DTO entity);

    void delete(DTO entity);

    void validateDTO(DTO dto);

}

