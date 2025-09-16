package app.DTO;

import java.util.Set;

public record DirectorDTO(
    Integer id,
    String name,
    int age,
    Set<Integer> actorIds
) implements DTO {
    @Override
    public Object getId() {
        return id;
    }
}
