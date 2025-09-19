package app.services;

import app.DAO.GenreDAO;
import app.DTO.GenreDTO;
import app.DTO.GenreListDTO;
import app.DTO.MovieDTO;
import app.DTO.ResponseDTO;
import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * GenreService - Clean, minimal implementation using generic AbstractService
 */
public class GenreService extends AbstractService<GenreDTO, Genre, Integer> {

    private final GenreDAO genreDAO;


    public GenreService(EntityManagerFactory emf) {
        super(emf, new GenreDAO(emf));
        this.genreDAO = (GenreDAO) dao; // Cast for additional methods
    }

    // ===========================================
    // CONVERSION METHODS - Only thing we need to implement!
    // ===========================================

    @Override
    protected GenreDTO convertToDTO(Genre genre) {
        return new GenreDTO(
            genre.getTmdbId(),
            genre.getGenreName()
        );
    }

    @Override
    protected Genre convertToEntity(GenreDTO dto) {
        return Genre.builder()
            .tmdbId(dto.id())
            .genreName(dto.genreName())
            .build();
    }

    @Override
    protected void validateDTO(GenreDTO dto) {
        super.validateDTO(dto);

        if (dto.genreName() == null || dto.genreName().trim().isEmpty()) {
            throw ApiException.badRequest("Genre name cannot be null or empty");
        }
    }

    // ===========================================
    // BUSINESS-SPECIFIC METHODS
    // ===========================================

    /**
     * Find genre by name
     */
    public GenreDTO findByName(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            throw ApiException.badRequest("Genre name cannot be null or empty");
        }
        
        return genreDAO.findByGenreName(genreName)
            .map(this::convertToDTO)
            .orElseThrow(() -> ApiException.notFound("Genre not found with name: " + genreName));
    }

    /**
     * Search genres by partial name match
     */
    public List<GenreDTO> searchByName(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            throw ApiException.badRequest("Genre name cannot be null or empty");
        }
        
        try {
            return genreDAO.findByGenreNameContaining(genreName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw ApiException.serverError("Failed to search genres by name: " + e.getMessage());
        }
    }

    /**
     * Get all movies for a specific genre
     */
    public List<String> getMoviesByGenre(Integer genreId) {
        Genre genre = dao.findById(genreId)
            .orElseThrow(() -> ApiException.notFound("Genre not found with ID: " + genreId));

        return genre.getMovies().stream()
            .map(movie -> movie.getTitle())
            .collect(Collectors.toList());
    }

    /**
     * Custom delete with business rules
     */
    @Override
    public void delete(Integer id) {
        if (id == null) {
            throw ApiException.badRequest("ID cannot be null");
        }

        Genre genre = dao.findById(id)
            .orElseThrow(() -> ApiException.notFound("Genre not found with ID: " + id));

        // Business rule: Cannot delete genre with movies
        if (!genre.getMovies().isEmpty()) {
            throw ApiException.conflict("Cannot delete genre with ID " + id + " because it has associated movies");
        }

        try {
            dao.delete(genre);
        } catch (Exception e) {
            throw ApiException.serverError("Failed to delete genre with ID " + id + ": " + e.getMessage());
        }
    }

    /**
     * Custom save with business rules
     */
    @Override
    public GenreDTO save(GenreDTO dto) {
        validateDTO(dto);

        // Check if genre with same name already exists
        if (genreDAO.findByGenreName(dto.genreName()).isPresent()) {
            throw ApiException.conflict("Genre already exists with name: " + dto.genreName());
        }

        return super.save(dto);
    }

    public void fetchAllGenres() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("language", "en");
                GenreListDTO response = makeApiRequestWithParams("/genre/movie/list", params, GenreListDTO.class);

                if (response != null && response.getGenres() != null) {
                    for (Object genreObj : response.getGenres()) {
                        try {
                            GenreDTO genreDTO = objectMapper.convertValue(genreObj, GenreDTO.class);
                            Optional<Genre> existingGenre = genreDAO.findByTmdbId(genreDTO.id());
                            if (existingGenre.isEmpty()) {
                                Genre entity = convertToEntity(genreDTO);
                                genreDAO.persist(entity);
                                System.out.println("Inserted new genre: " + genreDTO.genreName());
                            } else {
                                Genre existing = existingGenre.get();
                                existing.setGenreName(genreDTO.genreName());
                                genreDAO.update(existing);
                                System.out.println("Updated existing genre: " + genreDTO.genreName());
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to process genre: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (RuntimeException e) {
                System.err.println("Failed to process genre: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
}
