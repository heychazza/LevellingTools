package gg.plugins.levellingtools.hook;

import gg.plugins.levellingtools.tool.LevellingTool;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.entity.PlayerEntity;
import org.bukkit.OfflinePlayer;
import gg.plugins.levellingtools.LevellingTools;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion
{
    private LevellingTools plugin;
    
    public PlaceholderAPIHook(final LevellingTools plugin) {
        this.plugin = plugin;
    }
    
    public boolean canRegister() {
        return true;
    }
    
    public String getAuthor() {
        return "Chazza";
    }
    
    public String getIdentifier() {
        return "levellingtools";
    }
    
    public String getVersion() {
        return "1.0.0";
    }
    
    public String onRequest(final OfflinePlayer player, final String identifier) {
        final PlayerEntity playerEntity = PlayerEntity.getUser(player.getUniqueId());
        final LevellingTool nextLevel = (ConfigCache.getTools().size() > playerEntity.getLevel()) ? ConfigCache.getTools().get(playerEntity.getLevel() + 1) : ConfigCache.getTools().get(playerEntity.getLevel());
        if (identifier.equals("level")) {
            return String.valueOf(playerEntity.getLevel());
        }
        if (identifier.equals("blocks")) {
            return String.valueOf(playerEntity.getBlocksBroken());
        }
        if (identifier.equals("progress")) {
            return String.valueOf(playerEntity.getExperience() * 100 / nextLevel.getXpRequired());
        }
        if (identifier.equals("progress_bar")) {
            return LevellingTool.getProgressBar(playerEntity);
        }
        return null;
    }
}
