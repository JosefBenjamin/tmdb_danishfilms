package app.services;

import app.DAO.MovieDAO;
import app.DTO.MovieDTO;
import app.entities.Movie;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieService implements Service<MovieDTO, Integer> {

    private final MovieDAO movieDAO;
    private final EntityManagerFactory emf;

    public MovieService() {
        // Use HibernateConfig to get EntityManagerFactory
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.movieDAO = new MovieDAO(emf);
    }

    // Implementation of Service interface methods
    @Override
    public List<MovieDTO> getAll() {
        return getAllMovies();
    }

    @Override
    public MovieDTO getById(Integer id) {
        return getMovieById(id);
    }

    @Override
    public MovieDTO save(MovieDTO dto) {
        return saveMovie(dto);
    }

    @Override
    public MovieDTO update(MovieDTO dto) {
        return updateMovie(dto);
    }

    @Override
    public void delete(Integer id) {
        deleteMovie(id);
    }

    /**
     * Finds all movies in the database and converts to DTOs
     * @return List of MovieDTO objects
     */
    public List<MovieDTO> getAllMovies() {
        try {
            return movieDAO.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw MovieException.databaseError("retrieve all movies: " + e.getMessage());
        }
    }

    /**
     * Finds movie by ID and converts to DTO
     * @param id The movie ID
     * @return MovieDTO object
     * @throws MovieException if movie not found
     */
    public MovieDTO getMovieById(Integer id) {
        if (id == null || id <= 0) {
            throw MovieException.invalidTitle("Movie ID cannot be null or negative");
        }

        return movieDAO.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> MovieException.notFound(id));
    }

    /**
     * Saves a new movie
     * @param movieDTO The movie data to persist
     * @return Saved MovieDTO
     * @throws MovieException for validation or database errors
     */
    public MovieDTO saveMovie(MovieDTO movieDTO) {
        validateMovieDTO(movieDTO);

        try {
            Movie movie = convertToEntity(movieDTO);
            Movie savedMovie = movieDAO.persist(movie);
            return convertToDTO(savedMovie);
        } catch (Exception e) {
            throw MovieException.databaseError("persist movie: " + e.getMessage());
        }
    }

    /**
     * Updates an existing movie
     * @param movieDTO The movie data to update
     * @return Updated MovieDTO
     * @throws MovieException if movie not found or validation fails
     */
    public MovieDTO updateMovie(MovieDTO movieDTO) {
        if (movieDTO.id() == null) {
            throw MovieException.invalidTitle("Movie ID is required for update");
        }

        validateMovieDTO(movieDTO);

        // Check if movie exists
        if (movieDAO.findById(movieDTO.id()).isEmpty()) {
            throw MovieException.notFound(movieDTO.id());
        }

        try {
            Movie movie = convertToEntity(movieDTO);
            Movie updatedMovie = movieDAO.update(movie);
            return convertToDTO(updatedMovie);
        } catch (Exception e) {
            throw MovieException.databaseError("update movie: " + e.getMessage());
        }
    }

    /**
     * Deletes a movie by ID
     * @param id The movie ID to delete
     * @throws MovieException if movie not found
     */
    public void deleteMovie(Integer id) {
        if (id == null || id <= 0) {
            throw MovieException.invalidTitle("Movie ID cannot be null or negative");
        }

        Movie movie = movieDAO.findById(id)
                .orElseThrow(() -> MovieException.notFound(id));

        try {
            movieDAO.delete(movie);
        } catch (Exception e) {
            throw MovieException.databaseError("delete movie: " + e.getMessage());
        }
    }

    /**
     * Searches movies by title
     * @param title The title to search for
     * @return List of matching MovieDTO objects
     */
    public List<MovieDTO> searchMoviesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw MovieException.invalidTitle(title);
        }

        try {
            return movieDAO.findByTitle(title).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw MovieException.databaseError("search movies by title: " + e.getMessage());
        }
    }

    /**
     * Gets all movies by a specific director
     * @param directorId The director ID
     * @return List of MovieDTO objects
     */
    public List<MovieDTO> getMoviesByDirector(Integer directorId) {
        if (directorId == null || directorId <= 0) {
            throw MovieException.noDirectorAssigned(directorId);
        }

        try {
            return movieDAO.findByDirectorId(directorId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw MovieException.databaseError("get movies by director: " + e.getMessage());
        }
    }

    /**
     * Converts Movie entity to MovieDTO
     * @param movie The Movie entity
     * @return MovieDTO object
     */
    private MovieDTO convertToDTO(Movie movie) {
        Set<Integer> genreIds = movie.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Set<Integer> actorIds = movie.getActors().stream()
                .map(Actor::getId)
                .collect(Collectors.toSet());

        Integer directorId = movie.getDirector() != null ? movie.getDirector().getId() : null;

        return new MovieDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getReleaseYear(),
            movie.getOriginalLanguage(),
            genreIds,
            actorIds,
            directorId
        );
    }

    /**
     * Converts MovieDTO to Movie entity
     * @param movieDTO The MovieDTO
     * @return Movie entity
     */
    private Movie convertToEntity(MovieDTO movieDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = new Movie();
            movie.setId(movieDTO.id());
            movie.setTitle(movieDTO.title());
            movie.setReleaseYear(movieDTO.releaseYear());
            movie.setOriginalLanguage(movieDTO.originalLanguage());

            // Fetch and set genres if provided
            if (movieDTO.genreIds() != null && !movieDTO.genreIds().isEmpty()) {
                Set<Genre> genres = new java.util.HashSet<>();
                for (Integer genreId : movieDTO.genreIds()) {
                    Genre genre = em.find(Genre.class, genreId);
                    if (genre != null) {
                        genres.add(genre);
                    }
                }
                movie.setGenres(genres);
            }

            // Fetch and set actors if provided
            if (movieDTO.actorIds() != null && !movieDTO.actorIds().isEmpty()) {
                Set<Actor> actors = new java.util.HashSet<>();
                for (Integer actorId : movieDTO.actorIds()) {
                    Actor actor = em.find(Actor.class, actorId);
                    if (actor != null) {
                        actors.add(actor);
                    }
                }
                movie.setActors(actors);
            }

            // Fetch and set director if provided
            if (movieDTO.directorId() != null) {
                Director director = em.find(Director.class, movieDTO.directorId());
                if (director != null) {
                    movie.setDirector(director);
                }
            }

            return movie;
        } catch (Exception e) {
            throw MovieException.databaseError("convert DTO to entity: " + e.getMessage());
        }
    }

    /**
     * Validates MovieDTO data
     * @param movieDTO The MovieDTO to validate
     * @throws MovieException for validation errors
     */
    private void validateMovieDTO(MovieDTO movieDTO) {
        if (movieDTO == null) {
            throw MovieException.invalidTitle("Movie data cannot be null");
        }

        if (movieDTO.title() == null || movieDTO.title().trim().isEmpty()) {
            throw MovieException.invalidTitle(movieDTO.title());
        }

        if (movieDTO.releaseYear() != null) {
            int year = movieDTO.releaseYear();
            if (year < 1888 || year > LocalDate.now().getYear() + 10) {
                throw MovieException.invalidReleaseYear(year);
            }
        }
    }

    // Example usage method - can be removed in production
    public void demonstrateUsage() {
        List<MovieDTO> allMovies = getAllMovies();
        System.out.println("Found " + allMovies.size() + " movies:");

        for (MovieDTO movie : allMovies) {
            System.out.println("- " + movie.title() + " (" +
                (movie.releaseYear() != null ? movie.releaseYear() : "Unknown") + ")");
        }
    }
}
