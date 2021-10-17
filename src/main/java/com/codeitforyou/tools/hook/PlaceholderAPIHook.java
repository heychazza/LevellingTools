package com.codeitforyou.tools.hook;

import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.storage.PlayerData;
import com.codeitforyou.tools.util.Common;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private Tools plugin;

    public PlaceholderAPIHook(final Tools plugin) {
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
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
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
    public String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onRequest(final OfflinePlayer player, final String identifier) {
        final PlayerData playerData = PlayerData.get().get(player.getUniqueId());

        if (identifier.equals("level")) {
            return String.valueOf(playerData != null ? playerData.getLevel() : 1);
        }
        if (identifier.equals("blocks")) {
            return String.valueOf(playerData != null ? playerData.getBlocksBroken() : 0);
        }
        if (identifier.equals("progress")) {
            return String.valueOf(playerData != null ? Common.format(Common.getProgress(playerData)) : 0);
        }
        if (identifier.equals("xp")) {
            return String.valueOf(playerData != null ? Common.format(playerData.getXp()) : 0);
        }
        if (identifier.equals("progress_bar")) {
            return playerData != null ? Common.getProgressBar(playerData) : Common.getProgressBar(0);
        }
        return null;
    }
}
