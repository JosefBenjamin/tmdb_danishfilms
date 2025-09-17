package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

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
