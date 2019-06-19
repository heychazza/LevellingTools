package gg.plugins.levellingtools.entity;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.PrePersist;
import org.bson.types.ObjectId;

import java.util.Date;

public abstract class BaseEntity
{
    @Id
    ObjectId id;
    private Date creationDate;
    private Date lastChange;
    
    BaseEntity() {
    }
    
    public ObjectId getId() {
        return this.id;
    }
    
    public long getCreationDate() {
        return this.creationDate.getTime();
    }
    
    public long getLastChange() {
        return this.lastChange.getTime();
    }
    
    @PrePersist
    public void prePersist() {
        this.creationDate = ((this.creationDate == null) ? new Date() : this.creationDate);
        this.lastChange = ((this.lastChange == null) ? this.creationDate : new Date());
    }
    
    @Override
    public abstract String toString();
}
