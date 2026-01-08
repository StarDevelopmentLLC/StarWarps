package com.stardevllc.starwarps.events;

import com.stardevllc.starmclib.actors.Actor;
import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.Warp;
import org.bukkit.Location;
import org.bukkit.event.*;

import java.util.Optional;

/**
 * Called when a set warp request is initiated <br>
 * This is called before the warp is applied to the owner <br>
 * Cancel this event to prevent the set warp action from occuring
 *
 * @see StarWarps#setWarp(Actor, String, Location, String) 
 * @see StarWarps#setWarp(Actor, String, Location, String, Actor) 
 */
public class SetWarpEvent extends Event implements Cancellable {
    
    private static HandlerList handlerList = new HandlerList();
    
    private boolean cancelled;
    
    private final Warp warp;
    private final Actor actor;
    
    /**
     * Creates a new set warp event
     *
     * @param warp  The warp that is to be applied
     * @param actor The actor that performed the request (Can be null)
     */
    public SetWarpEvent(Warp warp, Actor actor) {
        this.warp = warp;
        this.actor = actor;
    }
    
    /**
     * The warp for the request
     *
     * @return The warp
     */
    public Warp getWarp() {
        return warp;
    }
    
    /**
     * The actor that requested the action
     *
     * @return The actor as an optional
     */
    public Optional<Actor> getActor() {
        return Optional.ofNullable(actor);
    }
    
    /**
     * Bukkit event things
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
