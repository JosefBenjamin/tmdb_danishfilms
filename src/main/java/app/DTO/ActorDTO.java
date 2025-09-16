package app.DTO;

import java.util.Set;

public record ActorDTO(
    Integer id,
    String name,
    int age,
    Set<Integer> directorIds
) implements DTO {
    @Override
    public Integer getId() {
        return id;
    }
}
