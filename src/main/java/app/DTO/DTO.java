package app.DTO;

/**
 * Base interface for all Data Transfer Objects (DTOs)
 * Provides common contract for all DTO implementations
 */
public interface DTO {
    /**
     * Gets the unique identifier for this DTO
     * @return the ID of the DTO
     */
    Integer getId();
}
