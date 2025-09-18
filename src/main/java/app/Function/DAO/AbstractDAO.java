package app.Function.DAO;

import app.Function.services.IService;
import app.Instance.DTO.IDTO;
import app.Instance.entities.IEntity;

import java.util.List;
import java.util.Optional;

public class AbstractDAO <  DTO     extends IDTO<ID>,
                            Entity  extends IEntity<ID>, ID>
        implements IDAO<DTO, Entity, ID> {

    private final IService<DTO, Entity, ID> service;

    public AbstractDAO(IService<DTO, Entity, ID> service) {
        this.service = service;
    }

    protected IService<DTO, Entity, ID> getService() {
        return service;
    }

    public Optional<Entity> findEntityById(ID id) {
        return service.getEntityById(id);
    }

    public List<Entity> findAllEntity(){
        return service.getAll().stream()
                .map(dto -> service.getEntityById(dto.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Entity persist(Entity entity) {
        return service.getEntityById(entity.getId())
                .orElseThrow(() -> new IllegalArgumentException("Entity with ID " + entity.getId() + " already exists"));
    }

    public Entity update(Entity entity) {
        return service.getEntityById(entity.getId())
                .orElseThrow(() -> new IllegalArgumentException("Entity with ID " + entity.getId() + " does not exist"));
    }

    public void delete(Entity entity) {
        service.delete(entity.getId());
    }

    public void validateDTO(DTO dto) {
        // Implement validation logic here
    }
}
