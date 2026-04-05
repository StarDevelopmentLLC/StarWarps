package com.stardevllc.starwarps.events;

import com.stardevllc.minecraft.actors.Actor;
import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.Warp;
import org.bukkit.event.*;

import java.util.Optional;

/**
 * Called when a warp is in the process of being deleted <br>
 * This is called before the deletion actually occurs <br>
 * Cancel this event to prevent the deletion to occur
 *
 * @see StarWarps#deleteWarp(String)
 * @see StarWarps#deleteWarp(String, Actor)
 */
public class DeleteWarpEvent extends Event implements Cancellable {
    
    private static HandlerList handlerList = new HandlerList();
    
    private boolean cancelled;
    
    private final Warp warp;
    private final Actor actor;
    
    /**
     * Creates a new delete warp event
     *
     * @param warp  The warp that is to be deleted
     * @param actor The actor that performed the deletion request (Can be null)
     */
    public DeleteWarpEvent(Warp warp, Actor actor) {
        this.warp = warp;
        this.actor = actor;
    }
    
    /**
     * The warp of the deletion request
     *
     * @return The warp
     */
    public Warp getWarp() {
        return warp;
    }
    
    /**
     * The actor that performed the deletion request (Can be null)
     *
     * @return The actor as an optional
     */
    public Optional<Actor> getActor() {
        return Optional.ofNullable(actor);
    }
    
    /**
     * Used for the Bukkit Events System
     *
     * @return The handler list
     */
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
