package app.services;

import app.DAO.*;
import app.DTO.*;
import app.entities.*;
import app.exceptions.*;
import jakarta.persistence.EntityManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generic Abstract Service providing CRUD operations and HTTP client functionality
 * @param <DTO> The Data Transfer Object type extending BaseDTO
 * @param <Entity> The Entity type extending BaseEntity
 * @param <ID> The ID type (Integer, Long, etc.)
 */
public abstract class AbstractService   <DTO    extends BaseDTO<ID>,
                                         Entity extends BaseEntity<ID>, ID>
        implements IService<DTO, ID> {

    // HTTP Client fields
    protected final ObjectMapper objectMapper;
    protected final String apiKey;
    protected final HttpClient httpClient;
    protected final String API_URL = "https://api.themoviedb.org/3";

    // Core dependencies
    protected final EntityManagerFactory emf;
    protected final IDAO<Entity, ID> dao;

    public AbstractService(EntityManagerFactory emf, IDAO<Entity, ID> dao) {
        this.emf = emf;
        this.dao = dao;

        // Initialize HTTP client
        this.apiKey = System.getenv("API_KEY");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.httpClient = HttpClient.newHttpClient();
    }

    // ===========================================
    // GENERIC CRUD OPERATIONS
    // ===========================================

    /**
     * Get all entities as DTOs
     */
    @Override
    public List<DTO> getAll() {
        try {
            return dao.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw ApiException.serverError("Failed to retrieve all entities: " + e.getMessage());
        }
    }

    /**
     * Get entity by ID as DTO
     */
    @Override
    public Optional<DTO> getById(ID id) {
        if (id == null) {
            throw ApiException.badRequest("ID cannot be null");
        }

        try {
            return dao.findById(id).map(this::convertToDTO);
        } catch (Exception e) {
            throw ApiException.serverError("Failed to retrieve entity with ID " + id + ": " + e.getMessage());
        }
    }

    /**
     * Save entity from DTO
     */
    @Override
    public DTO save(DTO dto) {
        validateDTO(dto);

        try {
            Entity entity = convertToEntity(dto);
            Entity savedEntity = dao.persist(entity);
            return convertToDTO(savedEntity);
        } catch (Exception e) {
            throw ApiException.serverError("Failed to save entity: " + e.getMessage());
        }
    }

    /**
     * Update entity from DTO
     */
    @Override
    public DTO update(DTO dto) {
        if (dto.getId() == null) {
            throw ApiException.badRequest("ID is required for update");
        }

        validateDTO(dto);

        // Check if entity exists
        if (dao.findById(dto.getId()).isEmpty()) {
            throw ApiException.notFound("Entity not found with ID: " + dto.getId());
        }

        try {
            Entity entity = convertToEntity(dto);
            Entity updatedEntity = dao.update(entity);
            return convertToDTO(updatedEntity);
        } catch (Exception e) {
            throw ApiException.serverError("Failed to update entity: " + e.getMessage());
        }
    }

    /**
     * Delete entity by ID
     */
    @Override
    public void delete(ID id) {
        if (id == null) {
            throw ApiException.badRequest("ID cannot be null");
        }

        Entity entity = dao.findById(id)
            .orElseThrow(() -> ApiException.notFound("Entity not found with ID: " + id));

        try {
            dao.delete(entity);
        } catch (Exception e) {
            throw ApiException.serverError("Failed to delete entity with ID " + id + ": " + e.getMessage());
        }
    }

    // ===========================================
    // HTTP CLIENT METHODS
    // ===========================================

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

    protected <T> T searchContent(String query, String contentType, Class<T> responseClass) {
        Map<String, String> params = new HashMap<>();
        params.put("query", query);
        return makeApiRequestWithParams("/search/" + contentType, params, responseClass);
    }

    // ===========================================
    // ABSTRACT METHODS - Must be implemented by concrete services
    // ===========================================

    /**
     * Convert Entity to DTO - must be implemented by each service
     */
    protected abstract DTO convertToDTO(Entity entity);

    /**
     * Convert DTO to Entity - must be implemented by each service
     */
    protected abstract Entity convertToEntity(DTO dto);

    /**
     * Validate DTO - can be overridden by each service for specific validation
     */
    protected void validateDTO(DTO dto) {
        if (dto == null) {
            throw ApiException.badRequest("DTO cannot be null");
        }
    }
}
