package app.services;

import app.DAO.DirectorDAO;
import app.DTO.DirectorDTO;
import app.entities.Director;
import app.entities.Actor;
import app.config.HibernateConfig;
import app.entities.Genre;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DirectorService extends AbstractService<DirectorDTO, Director> {

    private final DirectorDAO directorDAO;
    ApiException apiExc;

    public DirectorService(EntityManagerFactory emf) {
        super(emf);
        this.directorDAO = new DirectorDAO(HibernateConfig.getEntityManagerFactory());
    }

    // Implementation of Service interface methods
    public List<DirectorDTO> getAll() {
        return getAllDirectors();
    }

    public DirectorDTO getById(Integer id) {
        return getDirectorById(id);
    }

    public DirectorDTO save(DirectorDTO dto) {
        return saveDirector(dto);
    }

    public Director update(Director entity) {
        return (Director) updateEntity(entity);
    }

    public void delete(Integer id) {
        deleteDirector(id);
    }

    public List<Director> findAll() {
        try {
            return findAll(Director.class);
        } catch (Exception e) {
            throw apiExc.serverError("Cannot retrieve all genres: " + e.getMessage());
        }
    }

    /**
     * Finds all directors in the database and converts to DTOs
     * @return List of DirectorDTO objects
     */
    public List<DirectorDTO> getAllDirectors() {
        try {
            return directorDAO.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw apiExc.serverError("Could not retrieve all directors");
        }
    }

    /**
     * Finds director by ID and converts to DTO
     * @param id The director ID
     * @return DirectorDTO object
     */
    public DirectorDTO getDirectorById(Integer id) {
        if (id == null || id <= 0) {
            throw apiExc.badRequest("Director ID cannot be null or negative");
        }

        return directorDAO.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> apiExc.notFound("Director could not be found with ID: " + id));
    }

    /**
     * Saves a new director
     * @param directorDTO The director data to persist
     * @return Saved DirectorDTO
     */
    public DirectorDTO saveDirector(DirectorDTO directorDTO) {
        validateDirectorDTO(directorDTO);

        try {
            Director director = convertToEntity(directorDTO);
            Director savedDirector = directorDAO.persist(director);
            return convertToDTO(savedDirector);
        } catch (RuntimeException e) {
            throw apiExc.serverError("persist director: " + e.getMessage());
        }
    }

    /**
     * Updates an existing director
     * @param directorDTO The director data to update
     * @return Updated DirectorDTO
     */
    public DirectorDTO updateDirector(DirectorDTO directorDTO) {
        if (directorDTO.id() == null) {
            throw apiExc.badRequest("Director ID is required for update");
        }

        validateDirectorDTO(directorDTO);

        if (directorDAO.findById(directorDTO.id()).isEmpty()) {
            throw apiExc.notFound("Director could not be found with ID: " + directorDTO.id());
        }

        try {
            Director director = convertToEntity(directorDTO);
            Director updatedDirector = directorDAO.update(director);
            return convertToDTO(updatedDirector);
        } catch (RuntimeException e) {
            throw apiExc.serverError("Could not update director: " + e.getMessage());
        }
    }

    /**
     * Deletes a director by ID
     * @param id The director ID to delete
     */
    public void deleteDirector(Integer id) {
        if (id == null || id <= 0) {
            throw apiExc.badRequest("Director ID cannot be null or negative");
        }

        Director director = directorDAO.findById(id)
                .orElseThrow(() -> apiExc.notFound("Director could not be found with ID: " + id));

        if (!director.getMovies().isEmpty()) {
            throw apiExc.conflict("Cannot delete director with ID " + id + " because they have directed movies");
        }

        try {
            directorDAO.delete(director);
        } catch (Exception e) {
            throw apiExc.serverError("Could not delete director: " + e.getMessage());
        }
    }

    /**
     * Converts Director entity to DirectorDTO
     * @param director The Director entity
     * @return DirectorDTO object
     */
    public DirectorDTO convertToDTO(Director director) {
        Set<Integer> actorIds = director.getActors().stream()
                .map(Actor::getId)
                .collect(Collectors.toSet());

        return new DirectorDTO(
            director.getId(),
            director.getName(),
            director.getJob() != null ? director.getJob() : "Directing"
        );
    }

    /**
     * Converts DirectorDTO to Director entity
     * @param directorDTO The DirectorDTO
     * @return Director entity
     */
    public Director convertToEntity(DirectorDTO directorDTO) {
//        Director director = new Director();
//        director.setId(directorDTO.id());
//        director.setName(directorDTO.name());
//        return director;
        return super.convertToEntity(directorDTO);
    }

    /**
     * Validates DirectorDTO data
     * @param directorDTO The DirectorDTO to validate
     */
    private void validateDirectorDTO(DirectorDTO directorDTO) {
        if (directorDTO == null) {
            throw apiExc.badRequest("Director data cannot be null");
        }

        if (directorDTO.name() == null || directorDTO.name().trim().isEmpty()) {
            throw apiExc.badRequest("Directors name cannot be null");
        }


    }
}
