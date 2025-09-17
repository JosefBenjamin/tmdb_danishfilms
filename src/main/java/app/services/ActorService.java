package app.services;

import app.DAO.ActorDAO;
import app.DTO.ActorDTO;
import app.DTO.PersonDTO;
import app.DTO.ResponseDTO;
import app.entities.*;
import app.config.HibernateConfig;
import app.enums.ErrorType;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ActorService extends AbstractService<ActorDTO, Actor> {
//public class ActorService extends AbstractService<ActorDTO, Actor, Integer> implements Service<ActorDTO, Integer> {
    private final ActorDAO actorDAO;
    ApiException apiExc;

    public ActorService(EntityManagerFactory emf) {
        // Use HibernateConfig to get EntityManagerFactory
        super(emf);
        this.actorDAO = new ActorDAO(HibernateConfig.getEntityManagerFactory());
    }

    // Implementation of Service interface methods
//    @Override
//    public List<ActorDTO> getAll() {
//        return getAllActors();
//    }

//    @Override
//    public ActorDTO getById(Integer id) {
//        return getActorById(id);
//    }

//    @Override
//    public ActorDTO save(ActorDTO dto) {
//        ActorDTO result;
//        try {
//            result = AbstractService.saveEntity(dto);
//        } catch (ApiException e) {
//            throw new ApiException(e.getErrorType(), "Could not save actor: " + e.getMessage());
//    }
//        return result;
//    }

    public Actor update(Actor entity) {
        return (Actor) updateEntity(entity);
    }
//
//    @Override
//    public void delete(Integer id) {
//        deleteActor(id);
//    }
//
//    public Object convert(Object o) {
//        if (o instanceof Actor actor){
//            return convertToDTO(actor);
//        } else  if (o instanceof ActorDTO actorDTO) {
//            return convertToEntity(actorDTO);
//        } else {
//            throw new IllegalArgumentException("Invalid type for conversion to ActorDTO");
//        }
//    }

    /**
     * Finds all actors in the database and converts to DTOs
     * @return List of ActorDTO objects
     */
//    public List<ActorDTO> getAllActors() {
//        try {
//            return actorDAO.findAll().stream()
//                    .map(this::convertToDTO)
//                    .collect(Collectors.toList());
//        } catch (RuntimeException e) {
//            throw apiExc.badRequest("unable to retrieve any actors: " + e.getMessage());
//        }
//    }

    /**
     * Finds actor by ID and converts to DTO
     * @param id The actor ID
     * @return ActorDTO object
     */
//    public ActorDTO getActorById(Integer id) {
//        if (id == null || id <= 0) {
//            throw apiExc.badRequest("Actor ID cannot be null or negative");
//        }
//        return actorDAO.findById(id)
//                .map(this::getEntityById( id, Actor.class))
//                .orElseThrow(() -> apiExc.notFound("Actor not found with ID: " + id));
//    }


    public Optional<Actor> findById(Integer id){
        if (id == null || id <= 0) {
            throw apiExc.badRequest("Actor ID cannot be null or negative");
        }
        return actorDAO.findById(id);
    }



    /**
     * Deletes an actor by ID
     * @param id The actor ID to delete
     */
//    public void deleteActor(Integer id) {
//        if (id == null || id <= 0) {
//            throw apiExc.badRequest("Actor ID cannot be null or negative");
//        }
//
//        Actor actor = actorDAO.findById(id)
//                .orElseThrow(() -> apiExc.notFound("Actor not found with ID: " + id));
//
//        try {
//            actorDAO.delete(actor);
//        } catch (RuntimeException e) {
//            throw apiExc.serverError("Could not delete actor with ID: " + id);
//        }
//    }

    /**
     * Converts Actor entity to ActorDTO
     * @param actor The Actor entity
     * @return ActorDTO object
//     */
//    public ActorDTO convertToDTO(Actor actor) {
//        Set<Integer> directorIds = actor.getDirectors().stream()
//                .map(director -> director.getId())
//                .collect(Collectors.toSet());
//
//        return null;
//    }

    /**
     * Converts ActorDTO to Actor entity
     * @param actorDTO The ActorDTO
     * @return Actor entity
     */
    public Actor convertToEntity(ActorDTO actorDTO) {
        Actor actor = new Actor();
        actor.setId(actorDTO.id() != null ? actorDTO.id().intValue() : null);
        actor.setName(actorDTO.name());
        // Note: Directors would need to be fetched and set separately if needed
        return actor;
    }

    /**
     * Validates ActorDTO data
     * @param actorDTO The ActorDTO to validate
     */
    private void validateActorDTO(ActorDTO actorDTO) {
        if (actorDTO == null) {
            throw apiExc.badRequest("Actor data cannot be null");
        }

        if (actorDTO.name() == null || actorDTO.name().trim().isEmpty()) {
            throw apiExc.badRequest(actorDTO.name());
        }


    }

}



