package gg.plugins.levellingtools.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.util.MongoDB;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Entity(value = "player", noClassnameStored = true)
public class PlayerEntity extends BaseEntity {

    public static PlayerEntity getUser(UUID player) {
        if (MongoDB.isAvailable()) {

            PlayerEntity playerEntity = ConfigCache.getDB()
                    .createQuery(PlayerEntity.class)
                    .filter("uuid", player.toString())
                    .get();

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            if (playerEntity == null) {
                PlayerEntity newPlayerEntity = new PlayerEntity();
                newPlayerEntity.setUuid(player.toString());
                newPlayerEntity.setUsername(offlinePlayer.getName());
                newPlayerEntity.setLowercaseUsername(offlinePlayer.getName().toLowerCase());
                newPlayerEntity.setExperience(0);
                newPlayerEntity.setBlocksBroken(0);
                newPlayerEntity.setLevel(1);

                ConfigCache.getDB().save(newPlayerEntity);
                return newPlayerEntity;
            }
            return playerEntity;
        }
        return null;
    }

    public PlayerEntity() {
        super();
    }

    @Indexed
    private String uuid;
    private String username;
    private String lowercaseUsername;
    private int experience;
    private int blocksBroken;
    private int level;

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

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public int getLevel() {
        return level;
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

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return getClass().getName() + " [id=" + id + ", uuid=" + getUuid() + ", lowercaseUsername=" + getLowercaseUsername() + ", experience=" + getExperience() + "]";
    }
}
