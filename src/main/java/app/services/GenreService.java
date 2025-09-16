package app.services;

import app.DAO.GenreDAO;
import app.DTO.GenreDTO;
import app.entities.Genre;
import app.entities.Movie;
import app.config.HibernateConfig;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GenreService implements Service<GenreDTO, Integer> {

    private final GenreDAO genreDAO;

    public GenreService() {
        // Use HibernateConfig to get EntityManagerFactory
        this.genreDAO = new GenreDAO(HibernateConfig.getEntityManagerFactory());
    }

    /**
     * Finds all genres in the database and converts to DTOs
     * @return List of GenreDTO objects
     */
    public List<GenreDTO> getAllGenres() {
        try {
            return genreDAO.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw GenreException.databaseError("retrieve all genres: " + e.getMessage());
        }
    }

    /**
     * Finds genre by ID and converts to DTO
     * @param id The genre ID
     * @return GenreDTO object
     * @throws GenreException if genre not found
     */
    public GenreDTO getGenreById(Integer id) {
        if (id == null || id <= 0) {
            throw GenreException.invalidName("Genre ID cannot be null or negative");
        }
        
        return genreDAO.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> GenreException.notFound(id));
    }

    /**
     * Finds genre by name
     * @param genreName The genre name to search for
     * @return GenreDTO object
     * @throws GenreException if genre not found
     */
    public GenreDTO getGenreByName(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            throw GenreException.invalidName(genreName);
        }
        
        return genreDAO.findByGenreName(genreName)
                .map(this::convertToDTO)
                .orElseThrow(() -> GenreException.notFoundByName(genreName));
    }

    /**
     * Saves a new genre
     * @param genreDTO The genre data to persist
     * @return Saved GenreDTO
     * @throws GenreException for validation or database errors
     */
    public GenreDTO saveGenre(GenreDTO genreDTO) {
        validateGenreDTO(genreDTO);
        
        // Check if genre with same name already exists
        try {
            genreDAO.findByGenreName(genreDTO.genreName()).ifPresent(existingGenre -> {
                throw GenreException.alreadyExists(genreDTO.genreName());
            });
        } catch (GenreException e) {
            throw e;
        } catch (Exception e) {
            // Continue with persist if error is just that genre doesn't exist
        }
        
        try {
            Genre genre = convertToEntity(genreDTO);
            Genre savedGenre = genreDAO.persist(genre);
            return convertToDTO(savedGenre);
        } catch (Exception e) {
            throw GenreException.databaseError("persist genre: " + e.getMessage());
        }
    }

    /**
     * Updates an existing genre
     * @param genreDTO The genre data to update
     * @return Updated GenreDTO
     * @throws GenreException if genre not found or validation fails
     */
    public GenreDTO updateGenre(GenreDTO genreDTO) {
        if (genreDTO.id() == null) {
            throw GenreException.invalidName("Genre ID is required for update");
        }
        
        validateGenreDTO(genreDTO);
        
        // Check if genre exists
        if (!genreDAO.findById(genreDTO.id()).isPresent()) {
            throw GenreException.notFound(genreDTO.id());
        }
        
        try {
            Genre genre = convertToEntity(genreDTO);
            Genre updatedGenre = genreDAO.update(genre);
            return convertToDTO(updatedGenre);
        } catch (Exception e) {
            throw GenreException.databaseError("update genre: " + e.getMessage());
        }
    }

    /**
     * Deletes a genre by ID
     * @param id The genre ID to delete
     * @throws GenreException if genre not found or has associated movies
     */
    public void deleteGenre(Integer id) {
        if (id == null || id <= 0) {
            throw GenreException.invalidName("Genre ID cannot be null or negative");
        }
        
        Genre genre = genreDAO.findById(id)
                .orElseThrow(() -> GenreException.notFound(id));
        
        // Check if genre has associated movies (business rule)
        if (!genre.getMovies().isEmpty()) {
            throw GenreException.hasAssociatedMovies(id);
        }
        
        try {
            genreDAO.delete(genre);
        } catch (Exception e) {
            throw GenreException.databaseError("delete genre: " + e.getMessage());
        }
    }

    /**
     * Searches genres by partial name match
     * @param genreName The partial genre name to search for
     * @return List of matching GenreDTO objects
     */
    public List<GenreDTO> searchGenresByName(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            throw GenreException.invalidName(genreName);
        }
        
        try {
            return genreDAO.findByGenreNameContaining(genreName).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw GenreException.databaseError("search genres by name: " + e.getMessage());
        }
    }

    /**
     * Gets all movies for a specific genre
     * @param genreId The genre ID
     * @return List of movie titles
     * @throws GenreException if genre not found
     */
    public List<String> getMoviesByGenre(Integer genreId) {
        Genre genre = genreDAO.findById(genreId)
                .orElseThrow(() -> GenreException.notFound(genreId));

        return genre.getMovies().stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Converts Genre entity to GenreDTO
     * @param genre The Genre entity
     * @return GenreDTO object
     */
    private GenreDTO convertToDTO(Genre genre) {
        Set<Integer> movieIds = genre.getMovies().stream()
                .map(Movie::getId)
                .collect(Collectors.toSet());

        return new GenreDTO(
            genre.getId(),
            genre.getGenreName(),
            movieIds
        );
    }

    /**
     * Converts GenreDTO to Genre entity
     * @param genreDTO The GenreDTO
     * @return Genre entity
     */
    private Genre convertToEntity(GenreDTO genreDTO) {
        Genre genre = new Genre();
        genre.setId(genreDTO.id());
        genre.setGenreName(genreDTO.genreName());
        // Note: Movies would need to be fetched and set separately if needed
        return genre;
    }

    /**
     * Validates GenreDTO data
     * @param genreDTO The GenreDTO to validate
     * @throws GenreException for validation errors
     */
    private void validateGenreDTO(GenreDTO genreDTO) {
        if (genreDTO == null) {
            throw GenreException.invalidName("Genre data cannot be null");
        }

        if (genreDTO.genreName() == null || genreDTO.genreName().trim().isEmpty()) {
            throw GenreException.invalidName(genreDTO.genreName());
        }
    }

    // Implementation of Service interface methods
    @Override
    public List<GenreDTO> getAll() {
        return getAllGenres();
    }

    @Override
    public GenreDTO getById(Integer id) {
        return getGenreById(id);
    }

    @Override
    public GenreDTO save(GenreDTO dto) {
        return saveGenre(dto);
    }

    @Override
    public GenreDTO update(GenreDTO dto) {
        return updateGenre(dto);
    }

    @Override
    public void delete(Integer id) {
        deleteGenre(id);
    }

    // Example usage method - can be removed in production
    public void demonstrateUsage() {
        List<GenreDTO> allGenres = getAllGenres();
        System.out.println("Found " + allGenres.size() + " genres:");

        for (GenreDTO genre : allGenres) {
            System.out.println("- " + genre.genreName() + " (Movies: " + genre.movieIds().size() + ")");
        }
    }
}
