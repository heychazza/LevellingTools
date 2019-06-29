package gg.plugins.levellingtools.api;

import gg.plugins.levellingtools.entity.PlayerEntity;
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
    private Multiplier multiplier;
    private Block block;
    private PlayerEntity playerData;
    private LevellingTool tool;
    private static final HandlerList HANDLERS_LIST;
    private boolean isCancelled;

    public ToolMineEvent(final Player player, final ItemStack item, final double xpGained, final Multiplier multiplier, final Block block, final PlayerEntity playerData, final LevellingTool tool) {
        this.player = player;
        this.item = item;
        this.xpGained = xpGained;
        this.multiplier = multiplier;
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

    public Multiplier getMultiplier() {
        return multiplier;
    }

    public Block getBlock() {
        return this.block;
    }

    public PlayerEntity getPlayerData() {
        return this.playerData;
    }

    public LevellingTool getTool() {
        return this.tool;
    }

    public void setXpGained(final int xpGained) {
        this.xpGained = xpGained;
    }

    public void setMultiplier(Multiplier multiplier) {
        this.multiplier = multiplier;
    }

    static {
        HANDLERS_LIST = new HandlerList();
    }
}
