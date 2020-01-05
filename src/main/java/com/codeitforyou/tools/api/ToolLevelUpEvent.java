package com.codeitforyou.tools.api;

import com.codeitforyou.tools.storage.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ToolLevelUpEvent extends Event implements Cancellable {
    private Player player;
    private ItemStack item;
    private Block block;
    private PlayerData playerData;
    private Tool tool;
    private static final HandlerList HANDLERS_LIST;
    private boolean isCancelled;

    public ToolLevelUpEvent(final Player player, final ItemStack item, final Block block, final PlayerData playerData, final Tool tool) {
        this.player = player;
        this.item = item;
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
        return ToolLevelUpEvent.HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return ToolLevelUpEvent.HANDLERS_LIST;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public Block getBlock() {
        return this.block;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    public Tool getTool() {
        return this.tool;
    }

    static {
        HANDLERS_LIST = new HandlerList();
    }
}
