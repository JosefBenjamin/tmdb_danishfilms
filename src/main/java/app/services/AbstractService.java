package app.services;
import java.util.*;
import app.DAO.*;
import app.exceptions.ApiException;
import app.services.*;
import app.DTO.*;
import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public abstract class AbstractService<DTO, Entity, ID> {
    private static final String API_URL = "https://api.themoviedb.org/3";
    private final EntityManagerFactory emf;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AbstractService(EntityManagerFactory emf) {
        this.emf = emf;
        this.apiKey = System.getenv("API_KEY");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Generic method to make HTTP GET request to external API
     * @param endpoint The API endpoint (e.g., "/movie/123", "/person/456")
     * @param responseClass The class type to deserialize the response to
     * @param <T> The response type
     * @return The deserialized response object or null if error
     */
    protected <T> T makeApiRequest(String endpoint, Class<T> responseClass) {
        try {
            String url = API_URL + endpoint + (endpoint.contains("?") ? "&" : "?") + "api_key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), responseClass);
            } else {
                System.out.println("Error fetching data from API: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generic method to make HTTP GET request with custom URL parameters
     * @param endpoint The API endpoint
     * @param params Map of URL parameters
     * @param responseClass The class type to deserialize the response to
     * @param <T> The response type
     * @return The deserialized response object or null if error
     */
    protected <T> T makeApiRequestWithParams(String endpoint, Map<String, String> params, Class<T> responseClass) {
        try {
            StringBuilder urlBuilder = new StringBuilder(API_URL + endpoint + "?api_key=" + apiKey);

            for (Map.Entry<String, String> param : params.entrySet()) {
                urlBuilder.append("&").append(param.getKey()).append("=").append(param.getValue());
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlBuilder.toString()))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), responseClass);
            } else {
                System.out.println("Error fetching data from API: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generic method to get entity by ID from external API
     * Works for any entity type (Movie, Person, etc.)
     * @param entityType The entity type ("movie", "person", "tv", etc.)
     * @param entityId The entity ID
     * @param responseClass The DTO class to deserialize to
     * @param <T> The DTO type
     * @return The entity as DTO or null if not found
     */
    protected <T> T getEntityById(String entityType, int entityId, Class<T> responseClass) {
        return makeApiRequest("/" + entityType + "/" + entityId, responseClass);
    }

    /**
     * Generic method to search entities with filters
     * Works for discovering movies, TV shows, or searching people
     * @param endpoint The search/discover endpoint
     * @param filters Map of filter parameters
     * @param responseClass The response class (usually ResponseDTO)
     * @param <T> The response type
     * @return The search results
     */
    protected <T> T searchEntitiesWithFilters(String endpoint, Map<String, String> filters, Class<T> responseClass) {
        return makeApiRequestWithParams(endpoint, filters, responseClass);
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
     * Get movie by ID from external API
     * @param movieId The movie ID
     * @return MovieDTO object or null if not found
     */
    protected MovieDTO getMovieById(int movieId) {
        return getEntityById("movie", movieId, MovieDTO.class);
    }

    /**
     * Generic method to search for any type of content
     * @param query The search query
     * @param contentType The type of content ("movie", "person", "tv", "multi")
     * @param responseClass The response class
     * @param <T> The response type
     * @return Search results
     */
    protected <T> T searchContent(String query, String contentType, Class<T> responseClass) {
        Map<String, String> params = new HashMap<>();
        params.put("query", query);

        return makeApiRequestWithParams("/search/" + contentType, params, responseClass);
    }

    public BaseEntity convertToEntity(DTO dto){
        BaseEntity result = null;

        if (dto.getClass() == (ActorDTO.class) ){
            result = new Actor().builder()
                                    .name(((ActorDTO) dto).name())
                                    .build();
            return result;
        } else if (dto.getClass() == (DirectorDTO.class)){
            result = new Director().builder()
                                    .name(((DirectorDTO) dto).name())
                                    .job("Directing")
                                    .build();
            return result;
        } else if (dto.getClass() == (MovieDTO.class)){
            result = new Movie().builder()
                                        .title(String.valueOf(dto))
                                        .releaseYear((((MovieDTO) dto).releaseYear()))
                                        .originalLanguage(((MovieDTO) dto).originalLanguage())
                                        .build();
            return result;
        } else if (dto.getClass() == (GenreDTO.class)){
            result = new Genre().builder()
                                        .genreName(((GenreDTO) dto).genreName())
                                        .build();
            return result;
        } else return result;
    }

    public Record convertToDTO(Entity entity)
    {
        Record result = null;
        if (entity.getClass() == Actor.class){
            result = (ActorDTO) new ActorDTO(
                                        ((Actor) entity).getId(),
                                        ((Actor) entity).getName(),
                                        "Acting"
                                            );
                 return result;
        } else if (entity.getClass() == Director.class){
            result = (DirectorDTO) new DirectorDTO(
                                        ((Director) entity).getId(),
                                        ((Director) entity).getName(),
                                        "Directing"
                                            );
                 return result;
        } else if (entity.getClass() == Movie.class){
            result = (MovieDTO) new MovieDTO(
                                        ((Movie) entity).getId(),
                                        ((Movie) entity).getTitle(),
                                        ((Movie) entity).getReleaseYear(),
                                        ((Movie) entity).getOriginalLanguage(),
                                        new HashSet<Integer>()
                                            );
                 return result;

        } else if (entity.getClass() == Genre.class){
            result = (GenreDTO) new GenreDTO(
                                        ((Genre) entity).getId(),
                                        ((Genre) entity).getGenreName()
                                            );
                 return result;

        } else return result;
    }

    public  BaseEntity saveEntity(Entity entity) {
        BaseEntity result = null;
                try (EntityManager em = emf.createEntityManager()) {
                    em.getTransaction().begin();
                    try {
                        em.persist(entity);
                        em.getTransaction().commit();
                        if ( entity instanceof Actor){
                            result = new Actor().builder()
                                    .id(((Actor) entity).getId())
                                    .name(((Actor) entity).getName())
                                    .build();
                        } else if ( entity instanceof Director){
                            result = new Director().builder()
                                    .id(((Director) entity).getId())
                                    .name(((Director) entity).getName())
                                    .build();
                        } else if ( entity instanceof Movie){
                            result = new Movie().builder()
                                    .id(((Movie) entity).getId())
                                    .title(((Movie) entity).getTitle())
                                    .releaseYear(((Movie) entity).getReleaseYear())
                                    .originalLanguage(((Movie) entity).getOriginalLanguage())
                                    .build();
                        } else if ( entity instanceof Genre){
                            result = new Genre().builder()
                                    .id(((Genre) entity).getId())
                                    .genreName(((Genre) entity).getGenreName())
                                    .build();
                        }
                        return result;
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        throw e;
                    }
                }
         catch (Exception e) {
            throw ApiException.serverError("Could not save entity: " + e.getMessage());
        }
    }
}
