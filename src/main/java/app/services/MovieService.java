package app.services;

import app.DAO.MovieDAO;
import app.DAO.GenreDAO;
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
    private final GenreDAO genreDAO;

    public MovieService(EntityManagerFactory emf) {
        super(emf, new MovieDAO(emf));
        this.movieDAO = (MovieDAO) dao; // Cast for additional methods
        this.genreDAO = new GenreDAO(emf);
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
                    .id(dto.getId())
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
                        throw ApiException.notFound("Genre with id " + genreId + " not found");
                    }
                    genres.add(genre);
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

    public int fetchDanishMovies() {
        int imported = 0;
        int page = 1;
        Integer totalPages = null;

        // Use a 5-year window for Danish-language releases
        LocalDate from = LocalDate.now().minusYears(5);
        LocalDate to = LocalDate.now();

        // Plain Java HTTP + Jackson (no Spring)
        var http = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(15))
                .version(java.net.http.HttpClient.Version.HTTP_2)
                .build();
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        String apiKey = System.getenv("API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw ApiException.serverError("API_KEY environment variable is not set");
        }

        // Fetch TMDB genre list once to map IDs -> names
        Map<Integer, String> tmdbIdToNameMap = new HashMap<>();
        try {
            String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=en";
            var genreReq = java.net.http.HttpRequest.newBuilder(java.net.URI.create(genreUrl))
                    .header("accept", "application/json")
                    .GET()
                    .build();
            var genreResp = http.send(genreReq, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (genreResp.statusCode() / 100 != 2) {
                System.out.println("[TMDB] genre list status=" + genreResp.statusCode() + " body=" + genreResp.body());
                throw new RuntimeException("TMDB genre list HTTP " + genreResp.statusCode());
            }
            var root = mapper.readTree(genreResp.body()).get("genres");
            if (root != null && root.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode g : root) {
                    tmdbIdToNameMap.put(g.get("id").asInt(), g.get("name").asText());
                }
            }
        } catch (Exception e) {
            System.out.println("[TMDB] Failed to fetch genre list: " + e.getMessage());
        }

        EntityManager em = emf.createEntityManager();
        try {
        while (totalPages == null || page <= totalPages) {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("api_key", apiKey);
                params.put("with_original_language", "da");
                params.put("primary_release_date.gte", from.toString());
                params.put("primary_release_date.lte", to.toString());
                params.put("page", String.valueOf(page));

                String query = params.entrySet().stream()
                        .map(e -> java.net.URLEncoder.encode(e.getKey(), java.nio.charset.StandardCharsets.UTF_8) + "=" +
                                  java.net.URLEncoder.encode(e.getValue(), java.nio.charset.StandardCharsets.UTF_8))
                        .collect(java.util.stream.Collectors.joining("&"));

                String url = "https://api.themoviedb.org/3/discover/movie?" + query;

                System.out.println("[TMDB] GET " + url);
                var req = java.net.http.HttpRequest.newBuilder(java.net.URI.create(url))
                        .header("accept", "application/json")
                        .GET()
                        .build();
                var resp = http.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
                System.out.println("[TMDB] status=" + resp.statusCode());
                if (resp.statusCode() / 100 != 2) {
                    throw ApiException.serverError("TMDB error " + resp.statusCode() + ": " + resp.body());
                }

                // Parse typed paginated response
                ResponseDTO<MovieDTO> response = mapper.readValue(
                        resp.body(),
                        new com.fasterxml.jackson.core.type.TypeReference<ResponseDTO<MovieDTO>>() {}
                );

                if (response == null || response.results() == null || response.results().isEmpty()) {
                    break; // nothing more to import
                }

                if (totalPages == null) {
                    totalPages = response.totalPages();
                    if (totalPages == null) totalPages = 1;  // defensive for rare non-paged responses
                    totalPages = Math.min(totalPages, 500);  // TMDB caps pages to 500
                }

                for (MovieDTO movieDTO : response.results()) {
                    // Build entity WITHOUT forcing our DB primary key (let @GeneratedValue assign)
                    Movie entity = Movie.builder()
                            .title(movieDTO.title())
                            .releaseDate(movieDTO.releaseDate())
                            .originalLanguage(movieDTO.originalLanguage())
                            .build();

                    // Attach genres by TMDB ID (upsert if missing in DB)
                    if (movieDTO.genreIds() != null && !movieDTO.genreIds().isEmpty()) {
                        Set<Genre> genres = new HashSet<>();
                        for (Integer tmdbGenreId : movieDTO.genreIds()) {
                            Genre genre = genreDAO.findByTmdbId(tmdbGenreId)
                                    .orElseGet(() -> {
                                        Genre g = new Genre();
                                        g.setTmdbId(tmdbGenreId);
                                        g.setGenreName(tmdbIdToNameMap.getOrDefault(tmdbGenreId, "Unknown"));
                                        return genreDAO.persist(g);
                                    });
                            genres.add(genre);
                        }
                        entity.setGenres(genres);
                    }

                    // TODO: Prefer de-dup by a dedicated external key (tmdbId). Until then, be conservative:
                    boolean exists = movieDAO.findByTitle(entity.getTitle()).stream()
                            .anyMatch(m -> java.util.Objects.equals(m.getReleaseDate(), entity.getReleaseDate()));

                    if (!exists) {
                        movieDAO.persist(entity);
                        imported++;
                    }
                }

                page++;
            } catch (Exception ex) {
                Throwable cause = ex.getCause();
                String causeMsg = (cause == null ? "" : " | cause=" + cause.getClass().getSimpleName() + ": " + String.valueOf(cause.getMessage()));
                throw ApiException.serverError("Failed importing Danish movies page " + page + ": " + ex.getClass().getSimpleName() + " - " + String.valueOf(ex.getMessage()) + causeMsg);
            }
        }
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
        return imported;
    }


}
