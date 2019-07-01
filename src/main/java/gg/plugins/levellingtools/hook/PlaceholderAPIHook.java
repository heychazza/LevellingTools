package gg.plugins.levellingtools.hook;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.entity.PlayerEntity;
import gg.plugins.levellingtools.tool.LevellingTool;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private LevellingTools plugin;

    public PlaceholderAPIHook(final LevellingTools plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return plugin.getDescription().getName().toLowerCase();
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
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
            return String.valueOf(LevellingTool.getProgress(playerEntity, nextLevel));
        }
        if (identifier.equals("progress_bar")) {
            return LevellingTool.getProgressBar(playerEntity, nextLevel);
        }
        return null;
    }
}
