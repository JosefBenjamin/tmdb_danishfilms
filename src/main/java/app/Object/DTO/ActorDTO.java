package app.Object.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActorDTO(
    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String name,

    @JsonProperty("character")
    String job
) implements BaseDTO<Integer> {

    @Override
    public Integer getId() {
        return id;
    }
}
