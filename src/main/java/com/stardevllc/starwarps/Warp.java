package com.stardevllc.starwarps;

import com.stardevllc.Position;
import com.stardevllc.actors.Actor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Objects;

/**
 * Represents a warp
 */
public class Warp {
    private final Actor owner;
    private String name;
    private String worldName;
    private Position position;
    private String permission;
    
    private Location locationCache;
    
    /**
     * Constructs a new warp
     *
     * @param owner     The owner of the warp
     * @param name      The name of the warp
     * @param worldName The world name of the warp
     * @param position  The position of the warp
     */
    public Warp(Actor owner, String name, String worldName, Position position, String permission) {
        this.owner = owner;
        this.name = name;
        this.worldName = worldName;
        this.position = position;
        this.permission = permission;
    }
    
    /**
     * Constructs a new warp
     *
     * @param owner    The owner of the warp
     * @param name     The name of the warp
     * @param location The location of the warp
     */
    public Warp(Actor owner, String name, Location location, String permission) {
        this.owner = owner;
        this.name = name;
        this.worldName = location.getWorld().getName();
        this.position = Position.fromLocation(location);
        this.permission = permission;
    }
    
    /**
     * Sets the name of this warp
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the world name of this warp
     *
     * @param worldName The new world name
     */
    public void setWorldName(String worldName) {
        this.locationCache = null;
        this.worldName = worldName;
    }
    
    /**
     * Sets the position of this warp
     *
     * @param position The new position
     */
    public void setPosition(Position position) {
        this.locationCache = null;
        this.position = position;
    }
    
    /**
     * Sets the location of this warp
     *
     * @param location The new locatioin
     */
    public void setLocation(Location location) {
        setWorldName(location.getWorld().getName());
        setPosition(Position.fromLocation(location));
    }
    
    /**
     * Teleports an entity to this warp
     *
     * @param entity The entity
     */
    public void teleport(Entity entity) {
        if (this.locationCache == null) {
            this.locationCache = this.position.toLocation(Bukkit.getWorld(this.worldName));
        }
        
        entity.teleport(locationCache);
    }
    
    /**
     * The owner of the warp
     *
     * @return The owner
     */
    public Actor getOwner() {
        return owner;
    }
    
    /**
     * The name of the warp
     *
     * @return The name
     */
    public String getName() {
        return name;
    }
    
    /**
     * The world name of the warp
     *
     * @return The world name
     */
    public String getWorldName() {
        return worldName;
    }
    
    /**
     * The position of the warp
     *
     * @return The position
     */
    public Position getPosition() {
        return position;
    }
    
    /**
     * The permission of the warp
     * @return The permission
     */
    public String getPermission() {
        return permission;
    }
    
    @Override
    public final boolean equals(Object object) {
        if (!(object instanceof Warp warp)) {
            return false;
        }
        
        return Objects.equals(owner, warp.owner) && Objects.equals(name.toLowerCase(), warp.name.toLowerCase());
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hashCode(owner);
        result = 31 * result + Objects.hashCode(name);
        return result;
    }
}