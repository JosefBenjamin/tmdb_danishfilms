package app.Function.services;

import app.Function.DAO.ActorDAO;
import app.Instance.DTO.ActorDTO;
import app.Instance.entities.ActorEntity;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ActorService - Clean implementation using AbstractService with eager loading support
 */
public class ActorService extends AbstractService<ActorDTO, ActorEntity, Integer> {

    private final ActorDAO actorDAO;

    public ActorService(EntityManagerFactory emf) {
        super(emf, new ActorDAO(emf));
        this.actorDAO = (ActorDAO) dao; // Cast for additional methods
    }

    // ===========================================
    // CONVERSION METHODS - Required implementation from AbstractService
    // ===========================================

    @Override
    protected ActorDTO convertToDTO(ActorEntity actorEntity) {
        return new ActorDTO(
            actorEntity.getId(),
            actorEntity.getName(),
            "Acting" // Default job or could be extracted from entity if needed
        );
    }

    @Override
    protected ActorEntity convertToEntity(ActorDTO dto) {
        return ActorEntity.builder()
            .id(dto.getId())
            .name(dto.name())
            .age(25) // Default age, could be added to DTO if needed
            .build();
    }

    @Override
    protected void validateDTO(ActorDTO dto) {
        super.validateDTO(dto);

        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw ApiException.badRequest("Actor name cannot be null or empty");
        }
    }

    // ===========================================
    // BUSINESS-SPECIFIC METHODS - Using eager loading to avoid LazyInitializationException
    // ===========================================

    /**
     * Get all movies for a specific actor - uses eager loading with JOIN FETCH
     */
    public List<String> getMoviesByActor(Integer actorId) {
        if (actorId == null) {
            throw ApiException.badRequest("Actor ID cannot be null");
        }

        try {
            return actorDAO.findByIdWithMovies(actorId)
                .map(actorEntity -> {
                    // Collections are now eagerly loaded, safe to access
                    return actorEntity.getMovieEntities().stream()
                        .map(movie -> movie.getTitle())
                        .collect(Collectors.toList());
                })
                .orElseThrow(() -> ApiException.notFound("Actor not found with ID: " + actorId));
        } catch (Exception e) {
            throw ApiException.serverError("Failed to get movies for actor: " + e.getMessage());
        }
    }

    /**
     * Get all directors for a specific actor - uses eager loading with JOIN FETCH
     */
    public List<String> getDirectorsByActor(Integer actorId) {
        if (actorId == null) {
            throw ApiException.badRequest("Actor ID cannot be null");
        }

        try {
            return actorDAO.findByIdWithDirectors(actorId)
                .map(actorEntity -> {
                    // Collections are now eagerly loaded, safe to access
                    return actorEntity.getDirectorEntities().stream()
                        .map(director -> director.getName())
                        .collect(Collectors.toList());
                })
                .orElseThrow(() -> ApiException.notFound("Actor not found with ID: " + actorId));
        } catch (Exception e) {
            throw ApiException.serverError("Failed to get directors for actor: " + e.getMessage());
        }
    }
}
