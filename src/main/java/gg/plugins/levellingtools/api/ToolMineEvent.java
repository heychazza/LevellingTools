package gg.plugins.levellingtools.api;

import gg.plugins.levellingtools.storage.PlayerData;
import gg.plugins.levellingtools.tool.LevellingTool;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ToolMineEvent extends Event implements Cancellable {
    private Player player;
    private ItemStack item;
    private double xpGained;
    private Booster booster;
    private Block block;
    private PlayerData playerData;
    private LevellingTool tool;
    private static final HandlerList HANDLERS_LIST;
    private boolean isCancelled;

    public ToolMineEvent(final Player player, final ItemStack item, final double xpGained, final Booster booster, final Block block, final PlayerData playerData, final LevellingTool tool) {
        this.player = player;
        this.item = item;
        this.xpGained = xpGained;
        this.booster = booster;
        this.block = block;
        this.playerData = playerData;
        this.tool = tool;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return ToolMineEvent.HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return ToolMineEvent.HANDLERS_LIST;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public double getXpGained() {
        return this.xpGained;
    }

    public Booster getBooster() {
        return booster;
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBooster(Booster booster) {
        this.booster = booster;
    }

    public LevellingTool getTool() {
        return this.tool;
    }

    public void setXpGained(final int xpGained) {
        this.xpGained = xpGained;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    static {
        HANDLERS_LIST = new HandlerList();
    }
}
