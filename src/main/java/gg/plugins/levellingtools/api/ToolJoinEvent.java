package gg.plugins.levellingtools.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ToolJoinEvent extends Event implements Cancellable {
    private Player player;
    private ItemStack item;
    private int slot;
    private static final HandlerList HANDLERS_LIST;
    private boolean isCancelled;

    public ToolJoinEvent(final Player player, final ItemStack item, final int slot) {
        this.player = player;
        this.item = item;
        this.slot = slot;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return ToolJoinEvent.HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return ToolJoinEvent.HANDLERS_LIST;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public int getSlot() {
        return this.slot;
    }

    static {
        HANDLERS_LIST = new HandlerList();
    }
}
