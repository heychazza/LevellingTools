package net.chazza.levellingtools;

import de.tr7zw.itemnbtapi.NBTItem;
import de.tr7zw.itemnbtapi.utils.MinecraftVersion;
import net.chazza.levellingtools.entity.UserEntity;
import net.chazza.levellingtools.util.EnchantmentEnum;
import net.chazza.levellingtools.util.MongoDB;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OldLevellingTools {

    public void onEnable() {
        //Bukkit.getPluginManager().registerEvents(this, this);
       // saveDefaultConfig();
    }

    public String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String convertVariables(String message) {
        return message.replace("%level%", "1").replace("%mined%", "525");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        UserEntity userEntity = MongoDB.instance()
                .getDatabase()
                .createQuery(UserEntity.class)
                .filter("uuid", player.getUniqueId().toString())
                .get();

        if (userEntity == null) {
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

    @EventHandler
    public void onItemHold(BlockDamageEvent e) {

        Player player = e.getPlayer();
        ItemStack hand = player.getItemInHand();

        UserEntity userEntity = MongoDB.instance()
                .getDatabase()
                .createQuery(UserEntity.class)
                .filter("uuid", player.getUniqueId().toString())
                .get();

        MinecraftVersion.setLogging(false);
        NBTItem nbtItem = new NBTItem(hand);
        String owner = nbtItem.getString("owner");

        /* Not a Levelling Pick */
        if (!nbtItem.hasKey("owner")) {
            /* temp */
            nbtItem.setString("owner", player.getUniqueId().toString());
            player.setItemInHand(nbtItem.getItem());
            return;
        }

        UUID ownerUuid = UUID.fromString(nbtItem.getString("owner"));

        if(player.getUniqueId().equals(ownerUuid)) {
            player.sendMessage(ChatColor.YELLOW + "This is your levelling tool, a total of " + userEntity.getExperience() + " xp.");
        }

       // String name = convertVariables(translate(getConfig().getString("format.pickaxe.name")));
        List<String> lore = new ArrayList<>();
       // for (String loreStr : getConfig().getStringList("format.pickaxe.lore")) {
       //     lore.add(convertVariables(translate(loreStr)));
       // }

        //getLogger().info("Lookup for " + e.getPlayer().getName() + ":");
        System.out.println(hand.getItemMeta().getLore());
        for (String currentLore : hand.getItemMeta().getLore()) {
            if (currentLore.equals(translate(lore.get(0)))) {
                break;
            }

            //getLogger().info(EnchantmentEnum.valueOf("FORTUNE").getVanillaName());
            currentLore = ChatColor.stripColor(currentLore);

            if (!EnchantmentEnum.exists(currentLore.substring(0, currentLore.lastIndexOf(" ")).replace(" ", "_"))) {
               // getLogger().info("Found TE-Enchant: " + currentLore);

            } else {
                //getLogger().info("Found Vanilla Enchant: " + currentLore);
            }
        }
        //getLogger().info(" ");

        ItemMeta itemMeta = e.getPlayer().getItemInHand().getItemMeta();
       // itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        e.getPlayer().getItemInHand().setItemMeta(itemMeta);
    }
}
