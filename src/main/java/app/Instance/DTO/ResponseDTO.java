package app.Instance.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for handling TMDB API responses that contain lists of results
 */
public record ResponseDTO<DTO>(
    @JsonProperty("page") Integer page,
    @JsonProperty("results") List<DTO> results,
    @JsonProperty("total_pages") Integer totalPages,
    @JsonProperty("total_results") Integer totalResults
) {}
