package net.chazza.levellingtools.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;
import net.chazza.levellingtools.config.ConfigCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Entity(value = "user", noClassnameStored = true)
public class UserEntity extends BaseEntity {

    public static UserEntity getUser(UUID player) {
        UserEntity userEntity = ConfigCache.getDB()
                .createQuery(UserEntity.class)
                .filter("uuid", player.toString())
                .get();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        if (userEntity == null) {
            UserEntity newUserEntity = new UserEntity();
            newUserEntity.setUuid(player.toString());
            newUserEntity.setUsername(offlinePlayer.getName());
            newUserEntity.setLowercaseUsername(offlinePlayer.getName().toLowerCase());
            newUserEntity.setExperience(0);
            newUserEntity.setBlocksBroken(0);
            newUserEntity.setLevel(1);

            ConfigCache.getDB().save(newUserEntity);
            return newUserEntity;
        }
        return userEntity;
    }

    public UserEntity() {
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
