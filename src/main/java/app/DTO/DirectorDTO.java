package app.DTO;

import java.util.Set;

public record DirectorDTO(
    int id,
    String name,
    int age,
    Set<int> actorIds
) {}
