package app.Function.services;

import app.Function.DAO.MovieDAO;
import app.Instance.DTO.MovieDTO;
import app.Instance.DTO.ResponseDTO;
import app.Instance.entities.GenreEntity;
import app.Instance.entities.MovieEntity;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MovieService - Clean, minimal implementation using generic AbstractService
 */
public class MovieService extends AbstractService<MovieDTO, MovieEntity, Integer> {

    private final MovieDAO movieDAO;

    public MovieService(EntityManagerFactory emf) {
        super(emf, new MovieDAO(emf));
        this.movieDAO = (MovieDAO) dao; // Cast for additional methods
    }

    // ===========================================
    // CONVERSION METHODS - Only thing we need to implement!
    // ===========================================

    @Override
    protected MovieDTO convertToDTO(MovieEntity movieEntity) {
        Set<Integer> genreIds = movieEntity.getGenreEntities().stream()
                .map(GenreEntity::getId)
                .collect(Collectors.toSet());

        return new MovieDTO(
            movieEntity.getId(),
            movieEntity.getTitle(),
            movieEntity.getReleaseYear(),
            movieEntity.getOriginalLanguage(),
            genreIds
        );
    }

    @Override
    protected MovieEntity convertToEntity(MovieDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            MovieEntity movieEntity = MovieEntity.builder()
                .id(dto.getId())
                .title(dto.title())
                .releaseYear(dto.releaseYear())
                .originalLanguage(dto.originalLanguage())
                .build();

            // Fetch and set genres if provided
            if (dto.genreIds() != null && !dto.genreIds().isEmpty()) {
                Set<GenreEntity> genreEntities = new HashSet<>();
                for (Integer genreId : dto.genreIds()) {
                    GenreEntity genreEntity = em.find(GenreEntity.class, genreId);
                    if (genreEntity == null) {
                        throw ApiException.notFound("Genre with id " + genreId + " not found");
                    }
                    genreEntities.add(genreEntity);
                }
                movieEntity.setGenreEntities(genreEntities);
            }

            return movieEntity;
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

        if (dto.releaseYear() != null) {
            int year = dto.releaseYear();
            if (year < 1888 || year > LocalDate.now().getYear() + 10) {
                throw ApiException.badRequest("Invalid release year: " + year);
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
}
