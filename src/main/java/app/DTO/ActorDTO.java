package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ActorDTO(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("name")
        String name,

        @JsonProperty("character")
        String job,

        @JsonProperty("profile_path")
        String profilePath,

        @JsonProperty("cast_id")
        Integer castId,

        @JsonProperty("order")
        Integer order
) implements BaseDTO<Integer> {
    @Override
    public Integer getId() {
        return id;
    }
}
