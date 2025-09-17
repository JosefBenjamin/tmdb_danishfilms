package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record DirectorDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String name,

    @JsonProperty("job")
    String job

) {}
