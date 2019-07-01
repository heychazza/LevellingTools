package gg.plugins.levellingtools.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;
import gg.plugins.levellingtools.LevellingTools;

import java.util.UUID;

@Entity(value = "player", noClassnameStored = true)
public class PlayerEntity extends BaseEntity {
    @Indexed
    private String uuid;
    private String username;
    private String lowercaseUsername;
    private double experience;
    private int blocksBroken;
    private int level;

    public static PlayerEntity getUser(final UUID player) {
        return LevellingTools.getPlugin(LevellingTools.class).getPlayerEntities().get(player);
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getLowercaseUsername() {
        return this.lowercaseUsername;
    }

    public double getExperience() {
        return this.experience;
    }

    public int getBlocksBroken() {
        return this.blocksBroken;
    }

    public int getLevel() {
        return this.level;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setLowercaseUsername(final String lowercaseUsername) {
        this.lowercaseUsername = lowercaseUsername;
    }

    public void setExperience(final double experience) {
        this.experience = experience;
    }

    public void setBlocksBroken(final int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [id=" + this.id + ", uuid=" + this.getUuid() + ", lowercaseUsername=" + this.getLowercaseUsername() + ", experience=" + this.getExperience() + "]";
    }
}
