package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

/*
API reference: https://developer.themoviedb.org/reference/discover-movie
 */

public record MovieDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("title")
    String title,

    @JsonProperty("release_date")
    Integer releaseYear,

    @JsonProperty("original_language")
    String originalLanguage,

    @JsonProperty("genre_ids")
    Set<Integer> genreIds
)
{ }
