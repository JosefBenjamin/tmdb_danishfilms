package app.DTO;

import java.util.Set;

public record ActorDTO(
    Integer id,
    String name,
    Integer age,
    Set<Integer> directorIds
) {}
