package net.chazza.levellingtools;

import net.chazza.levellingtools.entities.UserEntity;
import net.chazza.levellingtools.util.MongoDB;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevellingTools extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
    }

    public String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String convertVariables(String message) {
        return message.replace("%level%", "1").replace("%mined%", "525");
    }


    public static boolean isVanillaEnchant(String ench) {
        HashMap<String, String> enchants = new HashMap<>();
        enchants.put("Power", "ARROW_DAMAGE");
        enchants.put("Flame", "ARROW_FIRE");
        enchants.put("Infinity", "ARROW_INFINITE");
        enchants.put("Punch", "ARROW_KNOCKBACK");
        enchants.put("Sharpness", "DAMAGE_ALL");
        enchants.put("Bane_Of_Arthropods", "DAMAGE_ARTHROPODS");
        enchants.put("Smite", "DAMAGE_UNDEAD");
        enchants.put("Depth_Strider", "DEPTH_STRIDER");
        enchants.put("Efficiency", "DIG_SPEED");
        enchants.put("Unbreaking", "DURABILITY");
        enchants.put("Fire_Aspect", "FIRE_ASPECT");
        enchants.put("Knockback", "KNOCKBACK");
        enchants.put("Fortune", "LOOT_BONUS_BLOCKS");
        enchants.put("Looting", "LOOT_BONUS_MOBS");
        enchants.put("Luck_Of_The_Sea", "LUCK");
        enchants.put("Lure", "LURE");
        enchants.put( "Respiration", "OXYGEN");
        enchants.put("Protection", "PROTECTION_ENVIRONMENTAL");
        enchants.put("Blast_Protection", "PROTECTION_EXPLOSIONS");
        enchants.put("Feather_Falling", "PROTECTION_FALL");
        enchants.put("Fire_Protection", "PROTECTION_FIRE");
        enchants.put("Projectile_Protection", "PROTECTION_PROJECTILE");
        enchants.put("Silk_Touch", "SILK_TOUCH");
        enchants.put("Thorns", "THORNS");
        enchants.put("Aqua_Affinity", "WATER_WORKER");
        enchants.put("Curse_Of_Binding", "BINDING_CURSE");
        enchants.put("Mending", "MENDING");
        enchants.put("Frost_Walker", "FROST_WALKER");
        enchants.put("Curse_Of_Vanishing", "VANISHING_CURSE");

        return enchants.containsKey(ench);
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

        String name = convertVariables(translate(getConfig().getString("format.pickaxe.name")));
        List<String> lore = new ArrayList<>();
        for (String loreStr : getConfig().getStringList("format.pickaxe.lore")) {
            lore.add(convertVariables(translate(loreStr)));
        }

        getLogger().info("Lookup for " + e.getPlayer().getName() + ":");
        for(String currentLore : e.getPlayer().getItemInHand().getItemMeta().getLore()) {
            if(currentLore.equals(translate(lore.get(0)))) {
                break;
            }

            currentLore = ChatColor.stripColor(currentLore);
            int strEnchInt = currentLore.lastIndexOf(" ");

            if(!isVanillaEnchant(currentLore.substring(0, strEnchInt).replace(" ", "_"))) {
                getLogger().info("Found TE-Enchant: " + ChatColor.stripColor(currentLore));
            } else {
                getLogger().info("Found Vanilla Enchant: " + ChatColor.stripColor(currentLore));
            }
        }
        getLogger().info(" ");

        ItemMeta itemMeta = e.getPlayer().getItemInHand().getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        e.getPlayer().getItemInHand().setItemMeta(itemMeta);
    }
}
