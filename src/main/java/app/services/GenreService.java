package app.services;

import app.DAO.GenreDAO;
import app.DTO.GenreDTO;
import app.entities.Genre;
import app.entities.Movie;
import app.config.HibernateConfig;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GenreService extends AbstractService<GenreDTO, Genre> {

    private final GenreDAO genreDAO;

    ApiException apiExc;

    public GenreService(EntityManagerFactory emf) {
        // Use HibernateConfig to get EntityManagerFactory
        super(emf);
        this.genreDAO = new GenreDAO(HibernateConfig.getEntityManagerFactory());
    }

    /**
     * Finds all genres in the database and converts to DTOs
     * @return List of GenreDTO objects
     */
    public List<Genre> findAll() {
        try {
            return findAll(Genre.class);
        } catch (Exception e) {
            throw apiExc.serverError("Cannot retrieve all genres: " + e.getMessage());
        }
    }

    /**
     * Finds genre by ID and converts to DTO
     * @param id The genre ID
     * @return GenreDTO object
     */
    public GenreDTO getGenreById(Integer id) {
        if (id == null || id <= 0) {
            throw apiExc.badRequest("Genre ID cannot be null or negative");
        }
        
        return genreDAO.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> apiExc.notFound("Cannot find genre with ID: " + id));
    }

    /**
     * Finds genre by name
     * @param genreName The genre name to search for
     * @return GenreDTO object
     */
    public GenreDTO getGenreByName(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            throw apiExc.badRequest(genreName);
        }
        
        return genreDAO.findByGenreName(genreName)
                .map(this::convertToDTO)
                .orElseThrow(() -> apiExc.notFound("Cannot find genre with the name: " + genreName));
    }

    /**
     * Saves a new genre
     * @param genreDTO The genre data to persist
     * @return Saved GenreDTO
     */
    public GenreDTO saveGenre(GenreDTO genreDTO) {
        validateGenreDTO(genreDTO);
        
        // Check if genre with same name already exists
        try {
            genreDAO.findByGenreName(genreDTO.genreName()).ifPresent(existingGenre -> {
                throw apiExc.alreadyExists("This genre already exists: " + genreDTO.genreName());
            });
        } catch (RuntimeException e) {
            throw e;
        }
        
        try {
            Genre genre = convertToEntity(genreDTO);
            Genre savedGenre = genreDAO.persist(genre);
            return convertToDTO(savedGenre);
        } catch (RuntimeException e) {
            throw apiExc.serverError("Could not persist genre: " + e.getMessage());
        }
    }

    /**
     * Updates an existing genre
     * @param genreDTO The genre data to update
     * @return Updated GenreDTO
//     */
//    public GenreDTO updateGenre(GenreDTO genreDTO) {
//        if (genreDTO.id() == null) {
//            throw apiExc.badRequest("Genre ID is required for update");
//        }
//
//        validateGenreDTO(genreDTO);
//
//        // Check if genre exists
//        if (!genreDAO.findById(genreDTO.id()).isPresent()) {
//            throw apiExc.notFound("Could not find genre with the ID: " + genreDTO.id());
//        }
//
//        try {
//            Genre genre = convertToEntity(genreDTO);
//            Genre updatedGenre = genreDAO.update(genre);
//            return convertToDTO(updatedGenre);
//        } catch (RuntimeException e) {
//            throw apiExc.serverError("update genre: " + e.getMessage());
//        }
//    }

    /**
     * Deletes a genre by ID
     * @param id The genre ID to delete
     */
    public void deleteGenre(Integer id) {
        if (id == null || id <= 0) {
            throw apiExc.badRequest("Genre ID cannot be null or negative");
        }
        
        Genre genre = genreDAO.findById(id)
                .orElseThrow(() -> apiExc.notFound("Could not find genre by ID: " + id));
        
        // Check if genre has associated movies (business rule)
        if (!genre.getMovies().isEmpty()) {
            throw apiExc.conflict("could not delete genre because the genre has movies" + id);
        }
        
        try {
            genreDAO.delete(genre);
        } catch (Exception e) {
            throw apiExc.serverError("Could not delete genre: " + e.getMessage());
        }
    }

    /**
     * Searches genres by partial name match
     * @param genreName The partial genre name to search for
     * @return List of matching GenreDTO objects
     */
    public List<GenreDTO> searchGenresByName(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            throw apiExc.badRequest("Genre name cannot be null or empty" + genreName);
        }
        
        try {
            return genreDAO.findByGenreNameContaining(genreName).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw apiExc.serverError("could not retrieve genres by name search " + e.getMessage());
        }
    }

    /**
     * Gets all movies for a specific genre
     * @param genreId The genre ID
     * @return List of movie titles
     */
    public List<String> getMoviesByGenre(Integer genreId) {
        Genre genre = genreDAO.findById(genreId)
                .orElseThrow(() -> apiExc.notFound("Could not find movies with genre ID: " + genreId));

        return genre.getMovies().stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Converts Genre entity to GenreDTO
     * @param genre The Genre entity
     * @return GenreDTO object
     */
    public GenreDTO convertToDTO(Genre genre) {
        Set<Integer> movieIds = genre.getMovies().stream()
                .map(Movie::getId)
                .collect(Collectors.toSet());

        return new GenreDTO(
            genre.getId(),
            genre.getGenreName()
        );
    }

    /**
     * Converts GenreDTO to Genre entity
     * @param genreDTO The GenreDTO
     * @return Genre entity
     */
    public Genre convertToEntity(GenreDTO genreDTO) {
           Genre genre = new Genre();
           genre.setId(genreDTO.id());
           genre.setGenreName(genreDTO.genreName());
        return genre;
        // Note: Movies would need to be fetched and set separately if needed
    }

    /**
     * Validates GenreDTO data
     * @param genreDTO The GenreDTO to validate
     */
    private void validateGenreDTO(GenreDTO genreDTO) {
        if (genreDTO == null) {
            throw apiExc.badRequest("Genre data cannot be null");
        }

        if (genreDTO.genreName() == null || genreDTO.genreName().trim().isEmpty()) {
            throw apiExc.badRequest("genre data cannot be null or empty " + genreDTO.genreName());
        }
    }

    // Implementation of Service interface methods
    public List<GenreDTO> getAll() {
        return getAllGenres();
    }

    public GenreDTO getById(Integer id) {
        return getGenreById(id);
    }

    public GenreDTO save(GenreDTO dto) {
        return saveGenre(dto);
    }

    public Genre update(Genre entity) {
        return (Genre) updateEntity(entity);
    }

    public void delete(Integer id) {
        deleteGenre(id);
    }

    // Example usage method - can be removed in production
    public void demonstrateUsage() {
        List<GenreDTO> allGenres = getAllGenres();
        System.out.println("Found " + allGenres.size() + " genres:");

        for (GenreDTO genre : allGenres) {
            //System.out.println("- " + genre.genreName() + " (Movies: " + genre.movieIds().size() + ")");
        }
    }
}
