package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for handling Person data from TMDB API (actors, directors, etc.)
 */
public record PersonDTO(
    @JsonProperty("id") Integer id,
    @JsonProperty("name") String name,
    @JsonProperty("biography") String biography,
    @JsonProperty("birthday") String birthday,
    @JsonProperty("place_of_birth") String placeOfBirth,
    @JsonProperty("profile_path") String profilePath,
    @JsonProperty("known_for_department") String knownForDepartment
) {}
