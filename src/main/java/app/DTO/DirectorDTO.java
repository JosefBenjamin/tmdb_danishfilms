package app.DTO;

import java.util.Set;

public record DirectorDTO(
    Long id,
    String name,
    int age,
    Set<Long> actorIds
) {}
