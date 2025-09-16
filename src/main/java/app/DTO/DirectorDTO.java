package app.DTO;

import java.util.Set;

public record DirectorDTO(
    Integer id,
    String name,
    Integer age,
    Set<Integer> actorIds
) {}
