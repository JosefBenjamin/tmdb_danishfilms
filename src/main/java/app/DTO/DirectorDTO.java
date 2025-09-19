package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectorDTO(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("name")
        String name,

        @JsonProperty("job")
        String job,

        @JsonProperty("profile_path")
        String profilePath,

        @JsonProperty("department")
        String department
) implements BaseDTO<Integer> {
    @Override
    public Integer getId() {
        return id;
    }
}
