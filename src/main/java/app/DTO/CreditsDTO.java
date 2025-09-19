package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreditsDTO(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("cast")
        List<ActorDTO> cast,

        @JsonProperty("crew")
        List<DirectorDTO> crew
) {}
