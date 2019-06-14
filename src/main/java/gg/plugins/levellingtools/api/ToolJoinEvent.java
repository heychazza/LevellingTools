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
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public ToolJoinEvent(Player player, ItemStack item, int slot) {
        this.player = player;
        this.item = item;
        this.slot = slot;
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

    public int getSlot() {
        return slot;
    }
}
