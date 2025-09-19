package app.services;

import app.DAO.DirectorDAO;
import app.DTO.DirectorDTO;
import app.entities.Director;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

/**
 * DirectorService - Clean, minimal implementation using generic AbstractService
 */
public class DirectorService extends AbstractService<DirectorDTO, Director, Integer> {

    public DirectorService(EntityManagerFactory emf) {
        super(emf, new DirectorDAO(emf));
    }

    // ===========================================
    // CONVERSION METHODS - Only thing we need to implement!
    // ===========================================

    @Override
    public DirectorDTO convertToDTO(Director director) {
        return new DirectorDTO(
                director.getId(),       // id
                director.getName(),     // name
                director.getJob(),      // job
                null,                  // profilePath (we don't store this in entity)
                "Directing"            // department
        );
    }


    @Override
    protected Director convertToEntity(DirectorDTO dto) {
        return Director.builder()
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

        Director director = dao.findById(id)
            .orElseThrow(() -> ApiException.notFound("Director not found with ID: " + id));

        // Business rule: Cannot delete director with movies
        if (!director.getMovies().isEmpty()) {
            throw ApiException.conflict("Cannot delete director with ID " + id + " because they have directed movies");
        }

        try {
            dao.delete(director);
        } catch (Exception e) {
            throw ApiException.serverError("Failed to delete director with ID " + id + ": " + e.getMessage());
        }
    }
}
