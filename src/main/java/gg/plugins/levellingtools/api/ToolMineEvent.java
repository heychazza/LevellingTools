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
    private int xpGained;
    private Block block;
    private PlayerEntity playerData;
    private LevellingTool tool;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public ToolMineEvent(Player player, ItemStack item, int xpGained, Block block, PlayerEntity playerData, LevellingTool tool) {
        this.player = player;
        this.item = item;
        this.xpGained = xpGained;
        this.block = block;
        this.playerData = playerData;
        this.tool = tool;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getXpGained() {
        return xpGained;
    }

    public Block getBlock() {
        return block;
    }

    public PlayerEntity getPlayerData() {
        return playerData;
    }

    public LevellingTool getTool() {
        return tool;
    }

    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }
}
