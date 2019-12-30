package com.codeitall.tools.storage.mongodb;

import com.codeitall.tools.storage.PlayerData;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.PrePersist;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "player", noClassnameStored = true)
public class MongoDBPlayerData implements PlayerData {
    @Id
    private ObjectId id;
    private Date creationDate;
    private Date lastChange;

    @Indexed
    private String uuid;
    private String username;
    private double xp;
    private int blocksBroken;
    private int level;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
}
