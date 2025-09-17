package app.services;

import app.DAO.MovieDAO;
import app.DTO.MovieDTO;
import app.DTO.ResponseDTO;
import app.entities.*;
import app.config.HibernateConfig;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MovieService extends AbstractService<MovieDTO, Movie> {

    private final MovieDAO movieDAO;
    private final EntityManagerFactory emf;
    AbstractService getClass;
    ApiException apiExc;

    public MovieService(EntityManagerFactory emf) {
        // Use HibernateConfig to get EntityManagerFactory
        super(emf);
        this.emf = emf;
        getClass = new AbstractService(emf) {};
        this.movieDAO = new MovieDAO(emf);
    }

    // Implementation of Service interface methods
    public List<Movie> getAll() {
        return findAll(Movie.class);
    }

    public Optional<Movie> getById(Integer id) {
        return movieDAO.findById(id);
    }


    public Movie update(Movie movie) {
        return (Movie) updateEntity(movie);
    }

    public void delete(Integer id) {
        deleteMovie(id);
    }

    /**
     * Finds all movies in the database and converts to DTOs
     * @return List of MovieDTO objects
     */
//    public List<MovieDTO> getAllMovies() {
//        try {
//            return movieDAO.findAll().stream()
//                    .map(this::convertToDTO)
//                    .collect(Collectors.toList());
//        } catch (RuntimeException e) {
//            throw apiExc.serverError("could not retrieve all movies: " + e.getMessage());
//        }
//    }

    /**
     * Finds movie by ID and converts to DTO
     * @param id The movie ID
     * @return MovieDTO object
     */
//    public MovieDTO getMovieById(Integer id) {
//        if (id == null || id <= 0) {
//            throw apiExc.badRequest("Movie ID cannot be null or negative");
//        }
//
//        return movieDAO.findById(id)
//                .map(this::convertToDTO)
//                .orElseThrow(() -> apiExc.notFound("Movie could not be found with ID: " + id));
//    }

    /**
     * Saves a new movie
     * @param movieDTO The movie data to persist
     * @return Saved MovieDTO
     */
//    public MovieDTO saveMovie(MovieDTO movieDTO) {
//        validateMovieDTO(movieDTO);
//
//        try {
//
//            return save(movieDTO);
//        } catch (RuntimeException e) {
//            throw apiExc.serverError("could not persist movie: " + e.getMessage());
//        }
//    }

    /**
     * Updates an existing movie
     * @param movieDTO The movie data to update
     * @return Updated MovieDTO
//     */
//    public MovieDTO updateMovie(MovieDTO movieDTO) {
//        if (movieDTO.id() == null) {
//            throw apiExc.badRequest("Movie ID is required for update");
//        }
//
//        validateMovieDTO(movieDTO);
//
//        // Check if movie exists
//        if (movieDAO.findById(movieDTO.id()).isEmpty()) {
//            throw apiExc.notFound("Movie could not be found with ID: " + movieDTO.id());
//        }
//
//        try {
//            Movie movie = AbstractService.getClass().convertToEntity(movieDTO);
//            Movie updatedMovie = movieDAO.update(movie);
//            return convertToDTO(updatedMovie);
//        } catch (RuntimeException e) {
//            throw apiExc.serverError("could not update movie: " + e.getMessage());
//        }
//    }

    /**
     * Deletes a movie by ID
     * @param id The movie ID to delete
     */
    public void deleteMovie(Integer id) {
        if (id == null || id <= 0) {
            throw apiExc.badRequest("Movie ID cannot be null or negative");
        }

        Movie movie = movieDAO.findById(id)
                .orElseThrow(() -> apiExc.notFound("Movie not found with ID: " + id));

        try {
            movieDAO.delete(movie);
        } catch (RuntimeException e) {
            throw apiExc.serverError("could not delete movie: " + e.getMessage());
        }
    }

    /**
     * Get movies by rating range from external API
     * @param min Minimum rating
     * @param max Maximum rating
     * @return List of MovieDTO objects
     */
    protected List<MovieDTO> getMoviesByRating(double min, double max) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vote_average.gte", String.valueOf(min));
            params.put("vote_average.lte", String.valueOf(max));

            ResponseDTO response = makeApiRequestWithParams("/discover/movie", params, ResponseDTO.class);

            if (response != null && response.results() != null) {
                return response.results();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Searches movies by title
     * @param title The title to search for
     * @return List of matching MovieDTO objects
     */
//    public List<MovieDTO> searchMoviesByTitle(String title) {
//        if (title == null || title.trim().isEmpty()) {
//            throw apiExc.badRequest("movie title cannot be null" + title);
//        }
//
//        try {
//            return movieDAO.findByTitle(title).stream()
//                    .map(this::convertToDTO)
//                    .collect(Collectors.toList());
//        } catch (RuntimeException e) {
//            throw apiExc.notFound("search movies by title: " + e.getMessage());
//        }
//    }

    /**
     * Gets all movies by a specific director
     * @param directorId The director ID
     * @return List of MovieDTO objects
     */
//    public List<MovieDTO> getMoviesByDirector(Integer directorId) {
//        if (directorId == null || directorId <= 0) {
//            throw apiExc.badRequest("director cannot be null" + directorId);
//        }
//
//        try {
//            return movieDAO.findByDirectorId(directorId).stream()
//                    .map(this::convertToDTO)
//                    .collect(Collectors.toList());
//        } catch (RuntimeException e) {
//            throw apiExc.notFound("could not get movies by director: " + e.getMessage());
//        }
//    }

    /**
     * Converts Movie entity to MovieDTO
     * @param movie The Movie entity
     * @return MovieDTO object
//     */
//    private MovieDTO convertToDTO(Movie movie) {
//        Set<Integer> genreIds = movie.getGenres().stream()
//                .map(Genre::getId)
//                .collect(Collectors.toSet());
//
//        Set<Integer> actorIds = movie.getActors().stream()
//                .map(Actor::getId)
//                .collect(Collectors.toSet());
//
//        Integer directorId = movie.getDirector() != null ? movie.getDirector().getId() : null;
//
//        return null;
//
//    }

    /**
     * Converts MovieDTO to Movie entity
     * @param movieDTO The MovieDTO
     * @return Movie entity
//     */
//    private Movie convertToEntity(MovieDTO movieDTO) {
//        try (EntityManager em = emf.createEntityManager()) {
//            Movie movie = new Movie();
//            movie.setId(movieDTO.id());
//            movie.setTitle(movieDTO.title());
//            movie.setReleaseYear(movieDTO.releaseYear());
//            movie.setOriginalLanguage(movieDTO.originalLanguage());
//
//            // Genres
//            if (movieDTO.genreIds() != null && !movieDTO.genreIds().isEmpty()) {
//                Set<Genre> genres = new HashSet<>();
//                for (Integer genreId : movieDTO.genreIds()) {
//                    Genre genre = em.find(Genre.class, genreId);
//                    if (genre == null) {
//                        throw ApiException.notFound("Genre with id " + genreId + " not found");
//                    }
//                    genres.add(genre);
//                }
//                movie.setGenres(genres);
//            }
//
////            // Actors
////            if (movieDTO.actorIds() != null && !movieDTO.actorIds().isEmpty()) {
////                Set<Actor> actors = new HashSet<>();
////                for (Integer actorId : movieDTO.actorIds()) {
////                    Actor actor = em.find(Actor.class, actorId);
////                    if (actor == null) {
////                        throw ApiException.notFound("Actor with id " + actorId + " not found");
////                    }
////                    actors.add(actor);
////                }
////                movie.setActors(actors);
////            }
////
////            // Director
////            if (movieDTO.directorId() != null) {
////                Director director = em.find(Director.class, movieDTO.directorId());
////                if (director == null) {
////                    throw ApiException.notFound("Director with id " + movieDTO.directorId() + " not found");
////                }
////                movie.setDirector(director);
////            }
//
//            return movie;
//        } catch (RuntimeException e) {
//            throw ApiException.serverError("convert DTO to entity failed: " + e.getMessage());
//        }
//    }


    /**
     * Validates MovieDTO data
     * @param movieDTO The MovieDTO to validate
     */
    private void validateMovieDTO(MovieDTO movieDTO) {
        if (movieDTO == null) {
            throw apiExc.badRequest("Movie data cannot be null");
        }

        if (movieDTO.title() == null || movieDTO.title().trim().isEmpty()) {
            throw apiExc.badRequest("Movie title cannot be null or empty");
        }

        if (movieDTO.releaseYear() != null) {
            int year = movieDTO.releaseYear();
            if (year < 1888 || year > LocalDate.now().getYear() + 10) {
                throw apiExc.badRequest("Invalid release year: " + year);
            }
        }
    }

}
