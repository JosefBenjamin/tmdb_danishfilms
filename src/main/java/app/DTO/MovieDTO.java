package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;
@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("title")
    String title,

    @JsonProperty("release_date")
    LocalDate releaseDate,

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
