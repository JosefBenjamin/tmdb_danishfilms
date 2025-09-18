package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/*
API Reference: https://developer.themoviedb.org/reference/movie-credits
 */

public record DirectorDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String name,

    @JsonProperty("job")
    String job
) implements BaseDTO<Integer> {
    @Override
    public Integer getId() {
        return id;
    }
}
