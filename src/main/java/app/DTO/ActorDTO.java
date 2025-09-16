package app.DTO;

import java.util.Set;

public record ActorDTO(
    int id,
    String name,
    int age,
    Set<int> directorIds
) {}
