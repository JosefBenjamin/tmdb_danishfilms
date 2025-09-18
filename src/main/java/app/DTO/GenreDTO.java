package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
Api reference: https://developer.themoviedb.org/reference/movie-details
 */

public record GenreDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String genreName
) {

}
