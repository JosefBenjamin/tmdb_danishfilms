package app.Instance.entities;
import lombok.Data.*;
/**
 * Base interface for all Entity classes
 * Provides common contract for all entity implementations
 */
public interface IEntity<ID> {
    /**
     * Gets the unique identifier for this entity
     * @return the ID of the entity
     */
    ID getId();
}
