package com.codeitall.tools.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ToolDamageEvent extends Event implements Cancellable {
    private Player player;
    private ItemStack item;
    private Block block;
    private static final HandlerList HANDLERS_LIST;
    private boolean isCancelled;

    public ToolDamageEvent(final Player player, final ItemStack item, final Block block) {
        this.player = player;
        this.item = item;
        this.block = block;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return ToolDamageEvent.HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return ToolDamageEvent.HANDLERS_LIST;
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

    static {
        HANDLERS_LIST = new HandlerList();
    }
}
