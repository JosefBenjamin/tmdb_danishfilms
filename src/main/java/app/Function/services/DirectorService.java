package app.Function.services;

import app.Function.DAO.DirectorDAO;
import app.Object.DTO.DirectorDTO;
import app.Object.entities.DirectorEntity;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

/**
 * DirectorService - Clean, minimal implementation using generic AbstractService
 */
public class DirectorService extends AbstractService<DirectorDTO, DirectorEntity, Integer> {

    public DirectorService(EntityManagerFactory emf) {
        super(emf, new DirectorDAO(emf));
    }

    // ===========================================
    // CONVERSION METHODS - Only thing we need to implement!
    // ===========================================

    @Override
    protected DirectorDTO convertToDTO(DirectorEntity directorEntity) {
        return new DirectorDTO(
            directorEntity.getId(),
            directorEntity.getName(),
            directorEntity.getJob() != null ? directorEntity.getJob() : "Directing"
        );
    }

    @Override
    protected DirectorEntity convertToEntity(DirectorDTO dto) {
        return DirectorEntity.builder()
            .id(dto.getId())
            .name(dto.name())
            .job(dto.job())
            .age(0) // Default age
            .build();
    }

    @Override
    protected void validateDTO(DirectorDTO dto) {
        super.validateDTO(dto);

        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw ApiException.badRequest("Director name cannot be null or empty");
        }
    }

    // ===========================================
    // BUSINESS-SPECIFIC METHODS
    // ===========================================

    /**
     * Custom delete with business rules
     */
    @Override
    public void delete(Integer id) {
        if (id == null) {
            throw ApiException.badRequest("ID cannot be null");
        }

        DirectorEntity directorEntity = dao.findById(id)
            .orElseThrow(() -> ApiException.notFound("Director not found with ID: " + id));

        // Business rule: Cannot delete director with movies
        if (!directorEntity.getMovieEntities().isEmpty()) {
            throw ApiException.conflict("Cannot delete director with ID " + id + " because they have directed movies");
        }

        try {
            dao.delete(directorEntity);
        } catch (Exception e) {
            throw ApiException.serverError("Failed to delete director with ID " + id + ": " + e.getMessage());
        }
    }
}
