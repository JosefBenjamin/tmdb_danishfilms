package app.DTO;

import java.util.Set;

public record ActorDTO(
    Long id,
    String name,
    int age,
    Set<Long> directorIds
) {}
