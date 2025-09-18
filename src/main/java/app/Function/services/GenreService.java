package app.Function.services;

import app.Function.DAO.GenreDAO;
import app.Instance.DTO.GenreDTO;
import app.Instance.entities.GenreEntity;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GenreService - Clean, minimal implementation using generic AbstractService
 */
public class GenreService extends AbstractService<GenreDTO, GenreEntity, Integer> {

    private final GenreDAO genreDAO;

    public GenreService(EntityManagerFactory emf) {
        super(emf, new GenreDAO(emf));
        this.genreDAO = (GenreDAO) dao; // Cast for additional methods
    }

    // ===========================================
    // CONVERSION METHODS - Only thing we need to implement!
    // ===========================================

    @Override
    protected GenreDTO convertToDTO(GenreEntity genreEntity) {
        return new GenreDTO(
            genreEntity.getId(),
            genreEntity.getGenreName()
        );
    }

    @Override
    protected GenreEntity convertToEntity(GenreDTO dto) {
        return GenreEntity.builder()
            .id(dto.getId())
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
        GenreEntity genreEntity = dao.findEntityById(genreId)
            .orElseThrow(() -> ApiException.notFound("Genre not found with ID: " + genreId));

        return genreEntity.getMovieEntities().stream()
            .map(movie -> movie.getTitle())
            .collect(Collectors.toList());
    }
}
