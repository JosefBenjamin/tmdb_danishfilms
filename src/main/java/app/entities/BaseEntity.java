package app.entities;

/**
 * Base interface for all Entity classes
 * Provides common contract for all entity implementations
 */
public abstract class BaseEntity {
    /**
     * Gets the unique identifier for this entity
     * @return the ID of the entity
     */
    public abstract Integer getId();

    /**
     * Sets the unique identifier for this entity
     * @param id the ID to set
     */
    public abstract void setId(Integer id);
}
