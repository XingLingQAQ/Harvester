package org.achymake.harvester.listeners.custom;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HarvestEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Block block;
    private boolean cancelled;
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    public Player getPlayer() {
        return player;
    }
    public Block getBlock() {
        return block;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HarvestEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }
}