package app.Function.services;

import app.Function.DAO.ActorDAO;
import app.Object.DTO.ActorDTO;
import app.Object.DTO.PersonDTO;
import app.Object.DTO.ResponseDTO;
import app.Object.entities.ActorEntity;
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

    // ===========================================
    // BUSINESS-SPECIFIC METHODS (using inherited HTTP client)
    // ===========================================

    /**
     * Search for actors by name using TMDB API
     */
    public List<PersonDTO> searchActorsByName(String actorName) {
        try {
            ResponseDTO response = searchContent(actorName, "person", ResponseDTO.class);

            if (response != null && response.results() != null) {
                // Convert MovieDTO results to PersonDTO (assuming TMDB returns person data)
                return response.results().stream()
                    .map(movieDto -> new PersonDTO(
                        movieDto.getId(),
                        movieDto.title(), // Using title as name
                        null, null, null, null, "Acting"
                    ))
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Get popular actors from TMDB API
     */
    public List<PersonDTO> getPopularActors() {
        try {
            ResponseDTO response = makeApiRequest("/person/popular", ResponseDTO.class);

            if (response != null && response.results() != null) {
                return response.results().stream()
                    .map(movieDto -> new PersonDTO(
                        movieDto.getId(),
                        movieDto.title(),
                        null, null, null, null, "Acting"
                    ))
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
