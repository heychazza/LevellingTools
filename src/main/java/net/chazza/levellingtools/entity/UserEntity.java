package net.chazza.levellingtools.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;

@Entity(value = "user", noClassnameStored = true)
public class UserEntity extends BaseEntity {

    public UserEntity() {
        super();
    }

    @Indexed
    protected String uuid;
    protected String username;
    protected String lowercaseUsername;
    protected int experience;

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getLowercaseUsername() {
        return lowercaseUsername;
    }

    public int getExperience() {
        return experience;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLowercaseUsername(String lowercaseUsername) {
        this.lowercaseUsername = lowercaseUsername;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Override
    public String toString() {
        return getClass().getName() + " [id=" + id + ", uuid=" + getUuid() + ", lowercaseUsername=" + getLowercaseUsername() + ", experience=" + getExperience() + "]";
    }
}
