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
}
