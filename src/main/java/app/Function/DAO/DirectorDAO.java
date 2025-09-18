package app.Function.DAO;

import app.Instance.DTO.DirectorDTO;
import app.Instance.entities.DirectorEntity;
import jakarta.persistence.EntityManagerFactory;

public class DirectorDAO extends AbstractDAO<DirectorDTO, DirectorEntity, Integer> {

    public DirectorDAO(EntityManagerFactory emf) {
        super(emf, DirectorEntity.class);
    }

    @Override
    public void validateDTO(DirectorDTO directorDTO) {
        super.validateDTO(directorDTO);
        if (directorDTO.name() == null || directorDTO.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Director name cannot be null or empty");
        }
    }
}
