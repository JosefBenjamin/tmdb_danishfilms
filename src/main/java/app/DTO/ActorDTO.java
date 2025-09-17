package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record ActorDTO(

    @JsonProperty("id")
    Integer id,

    @JsonProperty("name")
    String name,

    @JsonProperty("character")
    String job

) implements DTO {
    @Override
    public Integer getId() {
        return id;
    }

    /*
          "adult": false,
      "gender": 2,
      "id": 1019,
      "known_for_department": "Acting",
      "name": "Mads Mikkelsen",
      "original_name": "Mads Mikkelsen",
      "popularity": 3.3913,
      "profile_path": "/ntwPvV4GKGGHO3I7LcHMwhXfsw9.jpg",
      "cast_id": 5,
      "character": "Mads",
      "credit_id": "68bcc72c39edc42a531bc44a",
      "order": 0
     */
}
