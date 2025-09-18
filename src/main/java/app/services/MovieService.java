package app.services;

import app.DAO.MovieDAO;
import app.DTO.MovieDTO;
import app.DTO.ResponseDTO;
import app.entities.*;
import app.exceptions.ApiException;
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

    public void fetchDanishMovies() {
        int page = 1;
        int totalPages = 1;
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(1);
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


}
