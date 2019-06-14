package gg.plugins.levellingtools.api;

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
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public ToolDamageEvent(Player player, ItemStack item, Block block) {
        this.player = player;
        this.item = item;
        this.block = block;
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

    public Block getBlock() {
        return block;
    }
}
