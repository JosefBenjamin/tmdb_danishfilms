package app.services;
import java.util.*;
import app.DAO.*;
import app.config.HibernateConfig;
import app.exceptions.ApiException;
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
import java.util.stream.Collectors;

public abstract class AbstractService<DTO, Entity> {
    private static final String API_URL = "https://api.themoviedb.org/3";
    private static EntityManagerFactory emf;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    ApiException apiExc;

    public AbstractService(EntityManagerFactory emf) {
        AbstractService.emf = emf;
        this.apiKey = System.getenv("API_KEY");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Generic method to make HTTP GET request to external API
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
     * Generic method to search for any type of content
     */
    protected <T> T searchContent(String query, String contentType, Class<T> responseClass) {
        Map<String, String> params = new HashMap<>();
        params.put("query", query);
        return makeApiRequestWithParams("/search/" + contentType, params, responseClass);
    }

    /**
     * Generic convertToEntity method - Works with any DTO type
     * @param dto The DTO object to convert
     * @return The corresponding Entity object
     * @throws IllegalArgumentException if the DTO type is not supported
     */
    @SuppressWarnings("unchecked")
    public Entity convertToEntity(DTO dto) {
        if (dto == null) {
            return null;
        }

        // Use instanceof checks for compatibility with all Java versions
        if (dto instanceof ActorDTO) {
            ActorDTO actorDto = (ActorDTO) dto;
            return (Entity) Actor.builder()
                .id(actorDto.id())
                .name(actorDto.name())
                .build();
        }

        if (dto instanceof DirectorDTO) {
            DirectorDTO directorDto = (DirectorDTO) dto;
            return (Entity) Director.builder()
                .id(directorDto.id())
                .name(directorDto.name())
                .job(directorDto.job())
                .build();
        }

        if (dto instanceof MovieDTO) {
            MovieDTO movieDto = (MovieDTO) dto;
            return (Entity) Movie.builder()
                .id(movieDto.id())
                .title(movieDto.title())
                .releaseYear(movieDto.releaseYear())
                .originalLanguage(movieDto.originalLanguage())
                .build();
        }

        if (dto instanceof GenreDTO) {
            GenreDTO genreDto = (GenreDTO) dto;
            return (Entity) Genre.builder()
                .id(genreDto.id())
                .genreName(genreDto.genreName())
                .build();
        }

        throw new IllegalArgumentException("Unsupported DTO type: " + dto.getClass().getSimpleName());
    }

    /**
     * Generic convertToDTO method - Works with any Entity type
     * @param entity The Entity object to convert
     * @return The corresponding DTO object
     * @throws IllegalArgumentException if the Entity type is not supported
     */
    @SuppressWarnings("unchecked")
    public DTO convertToDTO(Entity entity) {
        if (entity == null) {
            return null;
        }

        if (entity instanceof Actor) {
            Actor actor = (Actor) entity;
            return (DTO) new ActorDTO(
                actor.getId(),
                actor.getName(),
                "Acting"
            );
        }

        if (entity instanceof Director) {
            Director director = (Director) entity;
            return (DTO) new DirectorDTO(
                director.getId(),
                director.getName(),
                director.getJob() != null ? director.getJob() : "Directing"
            );
        }

        if (entity instanceof Movie) {
            Movie movie = (Movie) entity;
            return (DTO) new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getOriginalLanguage(),
                new HashSet<>()
            );
        }

        if (entity instanceof Genre) {
            Genre genre = (Genre) entity;
            return (DTO) new GenreDTO(
                genre.getId(),
                genre.getGenreName()
            );
        }

        throw new IllegalArgumentException("Unsupported Entity type: " + entity.getClass().getSimpleName());
    }

    /**
     * Generic method to convert a list of DTOs to a list of Entities
     */
    public List<BaseEntity> dtoListToEntityList(List<DTO> dtoList) {
        return dtoList.stream()
                .map(this::convertToEntity)
                .map(entity -> (BaseEntity) entity)
                .collect(Collectors.toList());
    }


    /**
     * Saves an entity to the database
     * @param entity The entity to save
     * @return The saved entity with generated ID
     */
    public BaseEntity saveEntity(Entity entity) {
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

    /**
     * Updates an existing entity in the database
     * @param entity The entity to update
     * @return The updated entity
     */
    public Object updateEntity(Entity entity) {
        if (entity instanceof Movie){
            MovieDAO dao = new MovieDAO(emf);
            return dao.update((Movie) entity);

        } else if (entity instanceof Actor){
            ActorDAO dao = new ActorDAO(emf);
            return dao.update((Actor) entity);

        } else if (entity instanceof Director) {
            DirectorDAO dao = new DirectorDAO(emf);
            return dao.update((Director) entity);

        } else if (entity instanceof Genre) {
            GenreDAO dao = new GenreDAO(emf);
            return dao.update((Genre) entity);

        } else {
            throw apiExc.badRequest("Unsupported entity type for update");
        }
    }

    /**
     * Deletes an entity from the database
     * @param entity The entity to delete
     */
    public void delete(Entity entity){
        if (entity instanceof Movie){
           MovieDAO dao = new MovieDAO(emf);
           dao.delete((Movie) entity);

        } else if (entity instanceof Actor){
            ActorDAO dao = new ActorDAO(emf);
            dao.delete((Actor) entity);

        } else if (entity instanceof Director){
            DirectorDAO dao = new DirectorDAO(emf);
            dao.delete((Director) entity);

        } else if (entity instanceof Genre){
            GenreDAO dao = new GenreDAO(emf);
            dao.delete((Genre) entity);

        } else {
            throw apiExc.badRequest("Unsupported entity type for deletion");
        }
    }

    public Optional<Entity> findById(Integer id, Class<Entity> entityClass){
        try (EntityManager em = emf.createEntityManager()) {
            Entity entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    public List<Entity> findAll(Class<Entity> entityClass){
        try (EntityManager em = emf.createEntityManager()) {
            jakarta.persistence.TypedQuery<Entity> query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        }
    }
}
