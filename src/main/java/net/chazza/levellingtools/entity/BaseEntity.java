package net.chazza.levellingtools.entity;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.PrePersist;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Provide the BaseEntity implementation for all entity:
 *
 * ID, creation and last change date, version, their getters and setters (including @PrePersist),
 * and some abstract methods we'll require in the specific entity.
 */
public abstract class BaseEntity {

    @Id
    ObjectId id;

    /**
     * We'll only provide getters for these attributes, setting is done in @PrePersist.
     */
    private Date creationDate;
    private Date lastChange;

    BaseEntity() {
        super();
    }

    public ObjectId getId() {
        return id;
    }

    public long getCreationDate() {
        return creationDate.getTime();
    }

    public long getLastChange() {
        return lastChange.getTime();
    }

    @PrePersist
    public void prePersist() {
        this.creationDate = (creationDate == null) ? new Date() : creationDate;
        this.lastChange = (lastChange == null) ? creationDate : new Date();
    }

    public abstract String toString();

}

