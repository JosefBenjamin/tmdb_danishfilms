package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record GenreDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String genreName)
         {}
