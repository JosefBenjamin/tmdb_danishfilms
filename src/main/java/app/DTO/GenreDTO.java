package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenreDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String genreName
) implements BaseDTO<Integer> {

    @Override
    public Integer getId() {
        return id;
    }
}
