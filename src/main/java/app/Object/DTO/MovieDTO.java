package app.Object.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

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
) implements BaseDTO<Integer> {

    @Override
    public Integer getId() {
        return id;
    }
}
