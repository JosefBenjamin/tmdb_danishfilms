package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
API reference: https://developer.themoviedb.org/reference/movie-credits
 */

public record ActorDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String name,

    @JsonProperty("character")
    String job
)
{ }
