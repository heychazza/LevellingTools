package net.chazza.levellingtools;

import net.chazza.levellingtools.entities.UserEntity;
import net.chazza.levellingtools.util.MongoDB;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LevellingTools extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        UserEntity userEntity = MongoDB.instance()
                .getDatabase()
                .createQuery(UserEntity.class)
                .filter("uuid", player.getUniqueId().toString())
                .get();

        if(userEntity == null) {
            System.out.println("No user found for " + player.getName());

            UserEntity newUserEntity = new UserEntity();
            newUserEntity.setUuid(player.getUniqueId().toString());
            newUserEntity.setUsername(player.getName());
            newUserEntity.setLowercaseUsername(player.getName().toLowerCase());
            newUserEntity.setExperience(0);

            MongoDB.instance().getDatabase().save(newUserEntity);
        } else {
            System.out.println("Loaded information for " + player.getName());
            userEntity.setUuid(player.getUniqueId().toString());
            userEntity.setUsername(player.getName());
            userEntity.setLowercaseUsername(player.getName().toLowerCase());
            userEntity.setExperience(0);
            MongoDB.instance().getDatabase().save(userEntity);
        }
    }
}
