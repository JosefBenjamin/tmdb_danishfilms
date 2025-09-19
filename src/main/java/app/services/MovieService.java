package app.services;

import app.DAO.MovieDAO;
import app.DTO.MovieDTO;
import app.DTO.ResponseDTO;
import app.entities.*;
import app.exceptions.ApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MovieService - Clean, minimal implementation using generic AbstractService
 */
public class MovieService extends AbstractService<MovieDTO, Movie, Integer> {

    private final MovieDAO movieDAO;

    public MovieService(EntityManagerFactory emf) {
        super(emf, new MovieDAO(emf));
        this.movieDAO = (MovieDAO) dao; // Cast for additional methods
    }

    // ===========================================
    // CONVERSION METHODS - Only thing we need to implement!
    // ===========================================

    @Override
    protected MovieDTO convertToDTO(Movie movie) {
        Set<Integer> genreIds = movie.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        return new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getRating(),
                movie.getOriginalLanguage(),
                genreIds
        );
    }

    @Override
    protected Movie convertToEntity(MovieDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = Movie.builder()
                    .title(dto.title())
                    .releaseDate(dto.releaseDate())
                    .rating(dto.rating())
                    .originalLanguage(dto.originalLanguage())
                    .build();

            // Fetch and set genres if provided
            if (dto.genreIds() != null && !dto.genreIds().isEmpty()) {
                Set<Genre> genres = new HashSet<>();
                for (Integer genreId : dto.genreIds()) {
                    Genre genre = em.find(Genre.class, genreId);
                    if (genre == null) {
                        genres.add(genre);
                    } else {
                        throw ApiException.badRequest("Genre with ID " + genreId + " not found");
                    }
                }
                movie.setGenres(genres);
            }

            return movie;
        } catch (RuntimeException e) {
            throw ApiException.serverError("Failed to convert DTO to entity: " + e.getMessage());
        }
    }

    @Override
    protected void validateDTO(MovieDTO dto) {
        super.validateDTO(dto);

        if (dto.title() == null || dto.title().trim().isEmpty()) {
            throw ApiException.badRequest("Movie title cannot be null or empty");
        }

        if (dto.releaseDate() != null) {
            LocalDate releaseDate = dto.releaseDate();
            if ( releaseDate.isBefore(LocalDate.now().minusYears(5))) {
                throw ApiException.badRequest("Invalid release release date: " + releaseDate);
            }
        }
    }

    // ===========================================
    // BUSINESS-SPECIFIC METHODS
    // ===========================================

    /**
     * Search movies by title
     */
    public List<MovieDTO> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw ApiException.badRequest("Movie title cannot be null or empty");
        }

        try {
            return movieDAO.findByTitle(title).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw ApiException.serverError("Failed to search movies by title: " + e.getMessage());
        }
    }

    /**
     * Get movies by director
     */
    public List<MovieDTO> getByDirector(Integer directorId) {
        if (directorId == null || directorId <= 0) {
            throw ApiException.badRequest("Director ID cannot be null or negative");
        }

        try {
            return movieDAO.findByDirectorId(directorId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw ApiException.serverError("Failed to get movies by director: " + e.getMessage());
        }
    }

    /**
     * Get movies by rating range from external API
     */

    public List<MovieDTO> getMoviesByRating(double min, double max) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vote_average.gte", String.valueOf(min));
            params.put("vote_average.lte", String.valueOf(max));

            // Deserialize into ResponseDTO with raw LinkedHashMap
            ResponseDTO<?> response = makeApiRequestWithParams("/discover/movie", params, ResponseDTO.class);

            if (response != null && response.results() != null) {
                // Convert List<LinkedHashMap> to List<MovieDTO>
                List<MovieDTO> dtos = objectMapper.convertValue(
                        response.results(),
                        new TypeReference<List<MovieDTO>>() {}
                );
                return dtos;
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    /**
     * Fetch Danish movies released in the last 5 years from external API
     * and store/update them in the local database
     */
    public void fetchDanishMovies() {
        int page = 1;
        int totalPages = 1;
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);
        LocalDate now = LocalDate.now();

        while (page <= totalPages) {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("with_original_language", "da");
                params.put("primary_release_date.gte", fiveYearsAgo.toString());
                params.put("primary_release_date.lte", now.toString());
                params.put("page", String.valueOf(page));

                ResponseDTO response = makeApiRequestWithParams("/discover/movie", params, ResponseDTO.class);

                if (response != null && response.results() != null) {
                    for (Object movieObj : response.results()) {
                        try {
                            MovieDTO movieDTO = objectMapper.convertValue(movieObj, MovieDTO.class);
                            //Movie entity = convertToEntity(movieDTO);
                            Optional<Movie> existingMovie = movieDAO.findById(movieDTO.getId());

                            if (existingMovie.isEmpty()) {
                                // Movie doesn't exist - create new one
                                Movie entity = convertToEntity(movieDTO);
                                movieDAO.persist(entity);
                                System.out.println("Created new movie: " + movieDTO.title());
                            } else {
                                // Movie exists - update it
                                Movie existing = existingMovie.get();
                                existing.setTitle(movieDTO.title());
                                existing.setReleaseDate(movieDTO.releaseDate());
                                existing.setRating(movieDTO.rating());
                                existing.setOriginalLanguage(movieDTO.originalLanguage());

                                movieDAO.update(existing);
                                System.out.println("Updated existing movie: " + movieDTO.title());
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to process movie: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                    totalPages = response.totalPages();
                }
            } catch (RuntimeException e) {
                System.err.println("Failed to process movie: " + e.getMessage());
                throw new RuntimeException(e);
            }
            page++;
        }
    }

    /**
     * Print all movies currently in the database
     */
    public void printAllMovies() {
        try {
            List<MovieDTO> movies = getAll(); // uses AbstractService.getAll()
            if (movies.isEmpty()) {
                System.out.println("No movies found in the database.");
            } else {
                System.out.println("Movies in database:");
                for (MovieDTO movie : movies) {
                    System.out.printf("ID: %d | Title: %s | Release Date: %s | Rating: %d | Language: %s%n",
                            movie.id(),
                            movie.title(),
                            movie.releaseDate(),
                            movie.rating(),
                            movie.originalLanguage());
                }
            }
        } catch (Exception e) {
            throw ApiException.serverError("Failed to print movies: " + e.getMessage());
        }
    }

    public List<Movie> getTop10ByRating (){
        var em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Movie m ORDER BY m.rating desc", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Movie> getBottom10ByRating(){
        var em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Movie m ORDER BY m.rating asc", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Double getTotalAverageRating() {
        var em = emf.createEntityManager();
        try {
            Double AverageRating = em.createQuery(
                    "SELECT AVG(m.rating) FROM Movie m", Double.class
            ).getSingleResult();
            return AverageRating != null ? AverageRating : 0.0;
        } finally {
            em.close();
        }
    }


}
