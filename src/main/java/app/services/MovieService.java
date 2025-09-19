package app.services;

import app.DAO.MovieDAO;
import app.DTO.*;
import app.entities.*;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

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
                movie.getTmdbId(),
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
    /**
     * Fetch Danish movies released in the last 5 years from external API
     * and store/update them in the local database
     */
    public void fetchDanishMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            int page = 1;
            int totalPages = 1;
            LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);
            LocalDate now = LocalDate.now();

            while (page <= totalPages) {
                Map<String, String> params = new HashMap<>();
                params.put("with_original_language", "da");
                params.put("primary_release_date.gte", fiveYearsAgo.toString());
                params.put("primary_release_date.lte", now.toString());
                params.put("page", String.valueOf(page));

                ResponseDTO response = makeApiRequestWithParams("/discover/movie", params, ResponseDTO.class);

                if (response != null && response.results() != null) {
                    em.getTransaction().begin();
                    try {
                        for (Object movieObj : response.results()) {
                            // Convert Object to MovieDTO using ObjectMapper
                            MovieDTO movieDTO = objectMapper.convertValue(movieObj, MovieDTO.class);

                            // Try to find existing movie by TMDB ID
                            TypedQuery<Movie> query = em.createQuery(
                                    "SELECT m FROM Movie m WHERE m.tmdbId = :tmdbId", Movie.class);
                            query.setParameter("tmdbId", movieDTO.id());
                            List<Movie> existingMovies = query.getResultList();

                            if (existingMovies.isEmpty()) {
                                Movie movie = Movie.builder()
                                        .tmdbId(movieDTO.id())
                                        .title(movieDTO.title())
                                        .releaseDate(movieDTO.releaseDate())
                                        .originalLanguage(movieDTO.originalLanguage())
                                        .build();
                                em.persist(movie);
                                System.out.println("Created new movie: " + movie.getTitle());
                            } else {
                                Movie existing = existingMovies.get(0);
                                existing.setTitle(movieDTO.title());
                                existing.setReleaseDate(movieDTO.releaseDate());
                                existing.setOriginalLanguage(movieDTO.originalLanguage());
                                em.merge(existing);
                                System.out.println("Updated existing movie: " + existing.getTitle());
                            }
                        }
                        em.getTransaction().commit();
                        totalPages = response.totalPages();
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        System.err.println("Failed to process page " + page + ": " + e.getMessage());
                    }
                }
                page++;
            }
        }
    }

    public void fetchMovieCast() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> localMovies = movieDAO.findAll();

            for (Movie movie : localMovies) {
                String endpoint = "/movie/" + movie.getTmdbId() + "/credits";
                CreditsDTO credits = makeApiRequest(endpoint, CreditsDTO.class);

                if (credits != null) {
                    em.getTransaction().begin();
                    try {
                        Movie managedMovie = em.merge(movie);

                        // Process actors
                        if (credits.cast() != null) {
                            for (ActorDTO actorDTO : credits.cast()) {
                                TypedQuery<Actor> query = em.createQuery(
                                        "SELECT a FROM Actor a WHERE a.tmdbId = :tmdbId", Actor.class);
                                query.setParameter("tmdbId", actorDTO.id());
                                List<Actor> existingActors = query.getResultList();

                                Actor actor;
                                if (existingActors.isEmpty()) {
                                    actor = Actor.builder()
                                            .tmdbId(actorDTO.id())
                                            .name(actorDTO.name())
                                            .age(0)
                                            .build();
                                    em.persist(actor);
                                    System.out.println("Added new actor: " + actor.getName());
                                } else {
                                    actor = existingActors.get(0);
                                    actor.setName(actorDTO.name());
                                    actor = em.merge(actor);
                                }

                                if (!managedMovie.getActors().contains(actor)) {
                                    managedMovie.addActor(actor);
                                }
                            }
                        }

                        // Process directors
                        if (credits.crew() != null) {
                            for (DirectorDTO directorDTO : credits.crew()) {
                                if (isDirector(directorDTO)) {
                                    TypedQuery<Director> query = em.createQuery(
                                            "SELECT d FROM Director d WHERE d.tmdbId = :tmdbId", Director.class);
                                    query.setParameter("tmdbId", directorDTO.id());
                                    List<Director> existingDirectors = query.getResultList();

                                    Director director;
                                    if (existingDirectors.isEmpty()) {
                                        director = Director.builder()
                                                .tmdbId(directorDTO.id())
                                                .name(directorDTO.name())
                                                .job(directorDTO.job())
                                                .build();
                                        em.persist(director);
                                        System.out.println("Added new director: " + director.getName());
                                    } else {
                                        director = existingDirectors.get(0);
                                        director.setName(directorDTO.name());
                                        director.setJob(directorDTO.job());
                                        director = em.merge(director);
                                    }

                                    managedMovie.setDirector(director);
                                }
                            }
                        }

                        em.getTransaction().commit();
                        System.out.println("Successfully processed cast for movie: " + movie.getTitle());

                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        System.err.println("Failed to process cast for movie " + movie.getTitle() + ": " + e.getMessage());
                    }
                }
            }
        }
    }


    private boolean isDirector(DirectorDTO directorDTO) {
        return directorDTO.job() != null &&
                (directorDTO.job().equalsIgnoreCase("Director") ||
                        directorDTO.job().contains("Director") ||
                        directorDTO.department() != null &&
                                directorDTO.department().equalsIgnoreCase("Directing"));
    }



}
