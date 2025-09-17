package app.Instance.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenreDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String genreName
) implements IDTO<Integer> {

    @Override
    public Integer getId() {
        return id;
    }
}
