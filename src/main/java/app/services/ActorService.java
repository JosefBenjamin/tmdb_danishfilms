package app.services;

import app.DAO.ActorDAO;
import app.DTO.ActorDTO;
import app.entities.Actor;
import app.entities.Director;
import app.exceptions.ActorException;
import app.config.HibernateConfig;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ActorService implements Service<ActorDTO, Integer> {

    private final ActorDAO actorDAO;

    public ActorService() {
        // Use HibernateConfig to get EntityManagerFactory
        this.actorDAO = new ActorDAO(HibernateConfig.getEntityManagerFactory());
    }

    // Implementation of Service interface methods
    @Override
    public List<ActorDTO> getAll() {
        return getAllActors();
    }

    @Override
    public ActorDTO getById(Integer id) {
        return getActorById(id);
    }

    @Override
    public ActorDTO save(ActorDTO dto) {
        return saveActor(dto);
    }

    @Override
    public ActorDTO update(ActorDTO dto) {
        return updateActor(dto);
    }

    @Override
    public void delete(Integer id) {
        deleteActor(id);
    }

    /**
     * Finds all actors in the database and converts to DTOs
     * @return List of ActorDTO objects
     */
    public List<ActorDTO> getAllActors() {
        try {
            return actorDAO.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw ActorException.databaseError("retrieve all actors: " + e.getMessage());
        }
    }

    /**
     * Finds actor by ID and converts to DTO
     * @param id The actor ID
     * @return ActorDTO object
     * @throws ActorException if actor not found
     */
    public ActorDTO getActorById(Integer id) {
        if (id == null || id <= 0) {
            throw ActorException.invalidName("Actor ID cannot be null or negative");
        }

        return actorDAO.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> ActorException.notFound(id));
    }

    /**
     * Saves a new actor
     * @param actorDTO The actor data to save
     * @return Saved ActorDTO
     * @throws ActorException for validation or database errors
     */
    public ActorDTO saveActor(ActorDTO actorDTO) {
        validateActorDTO(actorDTO);

        try {
            Actor actor = convertToEntity(actorDTO);
            Actor savedActor = actorDAO.save(actor);
            return convertToDTO(savedActor);
        } catch (Exception e) {
            throw ActorException.databaseError("save actor: " + e.getMessage());
        }
    }

    /**
     * Updates an existing actor
     * @param actorDTO The actor data to update
     * @return Updated ActorDTO
     * @throws ActorException if actor not found or validation fails
     */
    public ActorDTO updateActor(ActorDTO actorDTO) {
        if (actorDTO.id() == null) {
            throw ActorException.invalidName("Actor ID is required for update");
        }

        validateActorDTO(actorDTO);

        if (actorDAO.findById(actorDTO.id().intValue()).isEmpty()) {
            throw ActorException.notFound(actorDTO.id().intValue());
        }

        try {
            Actor actor = convertToEntity(actorDTO);
            Actor updatedActor = actorDAO.update(actor);
            return convertToDTO(updatedActor);
        } catch (Exception e) {
            throw ActorException.databaseError("update actor: " + e.getMessage());
        }
    }

    /**
     * Deletes an actor by ID
     * @param id The actor ID to delete
     * @throws ActorException if actor not found
     */
    public void deleteActor(Integer id) {
        if (id == null || id <= 0) {
            throw ActorException.invalidName("Actor ID cannot be null or negative");
        }

        Actor actor = actorDAO.findById(id)
                .orElseThrow(() -> ActorException.notFound(id));

        try {
            actorDAO.delete(actor);
        } catch (Exception e) {
            throw ActorException.databaseError("delete actor: " + e.getMessage());
        }
    }

    /**
     * Converts Actor entity to ActorDTO
     * @param actor The Actor entity
     * @return ActorDTO object
     */
    private ActorDTO convertToDTO(Actor actor) {
        Set<Integer> directorIds = actor.getDirectors().stream()
                .map(director -> director.getId())
                .collect(Collectors.toSet());

        return new ActorDTO(
            actor.getId(),
            actor.getName(),
            actor.getAge(),
            directorIds
        );
    }

    /**
     * Converts ActorDTO to Actor entity
     * @param actorDTO The ActorDTO
     * @return Actor entity
     */
    private Actor convertToEntity(ActorDTO actorDTO) {
        Actor actor = new Actor();
        actor.setId(actorDTO.id() != null ? actorDTO.id().intValue() : null); 
        actor.setName(actorDTO.name());
        actor.setAge(actorDTO.age());
        // Note: Directors would need to be fetched and set separately if needed
        return actor;
    }

    /**
     * Validates ActorDTO data
     * @param actorDTO The ActorDTO to validate
     * @throws ActorException for validation errors
     */
    private void validateActorDTO(ActorDTO actorDTO) {
        if (actorDTO == null) {
            throw ActorException.invalidName("Actor data cannot be null");
        }

        if (actorDTO.name() == null || actorDTO.name().trim().isEmpty()) {
            throw ActorException.invalidName(actorDTO.name());
        }

        if (actorDTO.age() < 0 || actorDTO.age() > 150) {
            throw ActorException.invalidAge(actorDTO.age());
        }
    }

    // Example usage method - can be removed in production
    public void demonstrateUsage() {
        List<ActorDTO> allActors = getAllActors();
        System.out.println("Found " + allActors.size() + " actors:");

        for (ActorDTO actor : allActors) {
            System.out.println("- " + actor.name() + " (Age: " + actor.age() + ")");
        }
    }
}
