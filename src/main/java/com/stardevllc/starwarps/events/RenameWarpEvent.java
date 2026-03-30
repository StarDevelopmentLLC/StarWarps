package com.stardevllc.starwarps.events;

import com.stardevllc.actors.Actor;
import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.Warp;
import org.bukkit.event.*;

import java.util.Optional;

/**
 * Called when a rename warp request is initiated <br>
 * This event is called before the actual rename occurs <br>
 * Cancel this event to stop the rename
 *
 * @see StarWarps#renameWarp(String, String)
 * @see StarWarps#renameWarp(String, String, Actor)
 */
public class RenameWarpEvent extends Event implements Cancellable {
    
    private static HandlerList handlerList = new HandlerList();
    
    private boolean cancelled;
    
    private final Warp warp;
    private final String newName;
    private final Actor actor;
    
    /**
     * Creates a new rename warp event
     *
     * @param warp    The warp that the request is targetting
     * @param newName The new name for the warp
     * @param actor   The actor that performed the request (Can be null)
     */
    public RenameWarpEvent(Warp warp, String newName, Actor actor) {
        this.warp = warp;
        this.newName = newName;
        this.actor = actor;
    }
    
    /**
     * The warp of the rename request
     *
     * @return The warp to be renamed
     */
    public Warp getWarp() {
        return warp;
    }
    
    /**
     * The new name for the warp
     *
     * @return The new name
     */
    public String getNewName() {
        return newName;
    }
    
    /**
     * The actor of the request (Can be null)
     *
     * @return The actor as an optional
     */
    public Optional<Actor> getActor() {
        return Optional.ofNullable(actor);
    }
    
    /**
     * Bukkit Event stuff
     *
     * @return HandlerList
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
