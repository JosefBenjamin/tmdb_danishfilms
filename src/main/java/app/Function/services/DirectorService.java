package app.Function.services;

import app.Function.DAO.DirectorDAO;
import app.Instance.DTO.DirectorDTO;
import app.Instance.entities.DirectorEntity;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DirectorService - Clean implementation using generic AbstractService
 */
public class DirectorService extends AbstractService<DirectorDTO, DirectorEntity, Integer> {

    public DirectorService(EntityManagerFactory emf) {
        super(emf, new DirectorDAO(emf));
    }

    // ===========================================
    // CONVERSION METHODS - Required implementation from AbstractService
    // ===========================================

    @Override
    protected DirectorDTO convertToDTO(DirectorEntity directorEntity) {
        return new DirectorDTO(
            directorEntity.getId(),
            directorEntity.getName(),
            directorEntity.getJob()
        );
    }

    @Override
    protected DirectorEntity convertToEntity(DirectorDTO directorDTO) {
        return DirectorEntity.builder()
            .id(directorDTO.getId())
            .name(directorDTO.name())
            .job(directorDTO.job())
            .build();
    }
}

