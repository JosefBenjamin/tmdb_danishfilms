package app.DTO;

import java.util.Set;

public record GenreDTO(
    Integer id,
    String genreName,
    Set<Integer> movieIds
) implements DTO {
    @Override
    public Integer getId() {
        return id;
    }
}
