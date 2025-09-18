package app.Function.services;

import app.Function.DAO.ActorDAO;
import app.Instance.DTO.ActorDTO;
import app.Instance.DTO.ResponseDTO;
import app.Instance.entities.ActorEntity;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ActorService - Clean, minimal implementation using generic AbstractService
 */
public class ActorService extends AbstractService<ActorDTO, ActorEntity, Integer> {

    public ActorService(EntityManagerFactory emf) {
        super(emf, new ActorDAO(emf));
    }

    // ===========================================
    // CONVERSION METHODS - Now public for external access
    // ===========================================

    @Override
    public ActorDTO convertToDTO(ActorEntity actorEntity) {
        return new ActorDTO(
            actorEntity.getId(),
            actorEntity.getName(),
            "Acting"
        );
    }

    @Override
    public ActorEntity convertToEntity(ActorDTO dto) {
        return ActorEntity.builder()
            .id(dto.getId())
            .name(dto.name())
            .age(0) // Default age or get from DTO if available
            .build();
    }

    @Override
    protected void validateDTO(ActorDTO dto) {
        super.validateDTO(dto);

        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw ApiException.badRequest("Actor name cannot be null or empty");
        }
    }

    @Override
    public void delete(Integer integer) {
        dao.delete(dao.findEntityById(integer));
    }
}
