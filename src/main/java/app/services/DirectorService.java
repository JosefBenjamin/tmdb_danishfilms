package app.services;

import app.DAO.DirectorDAO;
import app.DTO.DirectorDTO;
import app.entities.Director;
import app.entities.Actor;
import app.entities.Movie;
import app.exceptions.DirectorException;
import app.config.HibernateConfig;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DirectorService {

    private final DirectorDAO directorDAO;

    public DirectorService() {
        // Use HibernateConfig to get EntityManagerFactory
        this.directorDAO = new DirectorDAO(HibernateConfig.getEntityManagerFactory());
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
        } catch (Exception e) {
            throw DirectorException.databaseError("retrieve all directors: " + e.getMessage());
        }
    }

    /**
     * Finds director by ID and converts to DTO
     * @param id The director ID
     * @return DirectorDTO object
     * @throws DirectorException if director not found
     */
    public DirectorDTO getDirectorById(Integer id) {
        if (id == null || id <= 0) {
            throw DirectorException.invalidName("Director ID cannot be null or negative");
        }

        return directorDAO.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> DirectorException.notFound(id));
    }

    /**
     * Saves a new director
     * @param directorDTO The director data to save
     * @return Saved DirectorDTO
     * @throws DirectorException for validation or database errors
     */
    public DirectorDTO saveDirector(DirectorDTO directorDTO) {
        validateDirectorDTO(directorDTO);

        try {
            Director director = convertToEntity(directorDTO);
            Director savedDirector = directorDAO.save(director);
            return convertToDTO(savedDirector);
        } catch (Exception e) {
            throw DirectorException.databaseError("save director: " + e.getMessage());
        }
    }

    /**
     * Updates an existing director
     * @param directorDTO The director data to update
     * @return Updated DirectorDTO
     * @throws DirectorException if director not found or validation fails
     */
    public DirectorDTO updateDirector(DirectorDTO directorDTO) {
        if (directorDTO.id() == null) {
            throw DirectorException.invalidName("Director ID is required for update");
        }

        validateDirectorDTO(directorDTO);

        // Check if director exists
        if (!directorDAO.findById(directorDTO.id()).isPresent()) {
            throw DirectorException.notFound(directorDTO.id());
        }

        try {
            Director director = convertToEntity(directorDTO);
            Director updatedDirector = directorDAO.update(director);
            return convertToDTO(updatedDirector);
        } catch (Exception e) {
            throw DirectorException.databaseError("update director: " + e.getMessage());
        }
    }

    /**
     * Deletes a director by ID
     * @param id The director ID to delete
     * @throws DirectorException if director not found or has associated movies
     */
    public void deleteDirector(Integer id) {
        if (id == null || id <= 0) {
            throw DirectorException.invalidName("Director ID cannot be null or negative");
        }

        Director director = directorDAO.findById(id)
                .orElseThrow(() -> DirectorException.notFound(id));

        // Check if director has movies (optional business rule)
        if (!director.getMovies().isEmpty()) {
            throw new DirectorException(409, "Cannot delete director with ID " + id + " because they have directed movies");
        }

        try {
            directorDAO.delete(director);
        } catch (Exception e) {
            throw DirectorException.databaseError("delete director: " + e.getMessage());
        }
    }

    /**
     * Gets all movies directed by a specific director
     * @param directorId The director ID
     * @return List of movie titles
     * @throws DirectorException if director not found
     */
    public List<String> getMoviesByDirector(int directorId) {
        Director director = directorDAO.findById(directorId)
                .orElseThrow(() -> DirectorException.notFound(directorId));

        if (director.getMovies().isEmpty()) {
            throw DirectorException.noMoviesDirected(directorId);
        }

        return director.getMovies().stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Converts Director entity to DirectorDTO
     * @param director The Director entity
     * @return DirectorDTO object
     */
    private DirectorDTO convertToDTO(Director director) {
        Set<Integer> actorIds = director.getActors().stream()
                .map(Actor::getId)
                .collect(Collectors.toSet());

        return new DirectorDTO(
            director.getId(),
            director.getName(),
            director.getAge(),
            actorIds
        );
    }

    /**
     * Converts DirectorDTO to Director entity
     * @param directorDTO The DirectorDTO
     * @return Director entity
     */
    private Director convertToEntity(DirectorDTO directorDTO) {
        Director director = new Director();
        director.setId(directorDTO.id());
        director.setName(directorDTO.name());
        director.setAge(directorDTO.age());
        // Note: Actors and Movies would need to be fetched and set separately if needed
        return director;
    }

    /**
     * Validates DirectorDTO data
     * @param directorDTO The DirectorDTO to validate
     * @throws DirectorException for validation errors
     */
    private void validateDirectorDTO(DirectorDTO directorDTO) {
        if (directorDTO == null) {
            throw DirectorException.invalidName("Director data cannot be null");
        }

        if (directorDTO.name() == null || directorDTO.name().trim().isEmpty()) {
            throw DirectorException.invalidName(directorDTO.name());
        }

        if (directorDTO.age() < 0 || directorDTO.age() > 150) {
            throw DirectorException.invalidAge(directorDTO.age());
        }
    }

    // Example usage method - can be removed in production
    public void demonstrateUsage() {
        List<DirectorDTO> allDirectors = getAllDirectors();
        System.out.println("Found " + allDirectors.size() + " directors:");

        for (DirectorDTO director : allDirectors) {
            System.out.println("- " + director.name() + " (Age: " + director.age() + ")");
        }
    }
}
