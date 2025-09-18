package app.services;

import app.DAO.ActorDAO;
import app.DTO.ActorDTO;
import app.DTO.MovieDTO;
import app.DTO.ResponseDTO;
import app.entities.Actor;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ActorService - Clean, minimal implementation using generic AbstractService
 */
public class ActorService extends AbstractService<ActorDTO, Actor, Integer> {

    public ActorService(EntityManagerFactory emf) {
        super(emf, new ActorDAO(emf));
    }

    // ===========================================
    // CONVERSION METHODS - Now public for external access
    // ===========================================

    @Override
    public ActorDTO convertToDTO(Actor actor) {
        return new ActorDTO(
            actor.getId(),
            actor.getName(),
            "Acting"
        );
    }

    @Override
    public Actor convertToEntity(ActorDTO dto) {
        return Actor.builder()
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

    /*
    public List<Actor> searchActorsByName(String actorName) {
        try {
            ResponseDTO response = searchContent(actorName, "actor", ResponseDTO.class);

            if (response != null && response.results() != null) {
                // Convert MovieDTO results to PersonDTO (assuming TMDB returns person data)
                return response.results().stream()
                    .map(movieDto -> new MovieDTO(
                        movieDto.(),
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
    /*
    public List<Actor> getPopularActors() {
        try {
            ResponseDTO response = makeApiRequest("/person/popular", ResponseDTO.class);

            if (response != null && response.results() != null) {
                return response.results().stream()
                    .map(movieDto -> new Actor(
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


     */


}
