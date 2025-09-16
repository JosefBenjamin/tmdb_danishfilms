package app.DTO;

import java.time.LocalDate;
import java.util.Set;

public record MovieDTO(
    Integer id,
    String title,
    Integer releaseYear,
    String originalLanguage,
    Set<Integer> genreIds,
    Set<Integer> actorIds,
    Integer directorId
) implements DTO {
    @Override
    public Integer getId() {
        return id;
    }
}
