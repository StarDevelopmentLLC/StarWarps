package com.stardevllc.starwarps;

import com.stardevllc.config.Section;
import com.stardevllc.config.file.FileConfig;
import com.stardevllc.config.file.yaml.YamlConfig;
import com.stardevllc.starlib.collections.observable.map.ObservableHashMap;
import com.stardevllc.starlib.collections.observable.map.ObservableMap;
import com.stardevllc.starwarps.events.*;
import com.stardevllc.Position;
import com.stardevllc.actors.Actor;
import com.stardevllc.actors.Actors;
import com.stardevllc.plugin.ExtendedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * Main class for the StarWarps logic.
 * This class must be initialized by a plugin by calling the {@link #init(ExtendedJavaPlugin)} method
 * This also has pretty much all logic needed for warps for the most part.
 * The warps are stored in an {@link ObservableMap} with Strings for the keys mapping to the warp itself
 * Using the observable map allows listening for changes
 * This also allows you to modify the warps as well, use with caution
 * </p>
 */
public final class StarWarps {
    private StarWarps() {}
    
    private static ExtendedJavaPlugin plugin;
    
    private static final ObservableMap<String, Warp> warps = new ObservableHashMap<>();
    
    /**
     * Initalilzes StarWarps
     *
     * @param plugin The holder plugin
     */
    public static void init(ExtendedJavaPlugin plugin) {
        if (StarWarps.plugin != null) {
            plugin.getLogger().warning("StarWarps has already been initialized by the plugin: " + StarWarps.plugin.getName());
            return;
        }
        
        StarWarps.plugin = plugin;
        
        plugin.getMainConfig().addDefault("warps.storage", "SINGLEFILE", "This controls how warps are stored.", "SINGLEFILE means that all warps are stored in a single file", "SEPARATEFILES means that warps are stored in individual files. It is recommended to change names using the command or when the server is offline");
        plugin.getMainConfig().addDefault("warps.singlefile.name", "warps.yml", "This is just the name of the file when using SINGLEFILE storage mode", "If you change this after warps are created, you will need to rename the old file to the new name. Otherwise warps will not be loaded");
        plugin.getMainConfig().addDefault("warps.separatefiles.foldername", "warps", "This is the name of the folder for the warp file locations when using SEPARATEFILES mode", "If you change this after warps are created, you will need to rename the old folder to the new name. Otherwise warps will not be loaded");
        plugin.getMainConfig().save();
        loadWarps();
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, StarWarps::saveWarp, 1L, 6000L);
    }
    
    private static Optional<File> getSingleFile() {
        if (!"singlefile".equalsIgnoreCase(plugin.getMainConfig().getString("warps.storage"))) {
            return Optional.empty();
        }
        
        File file = new File(plugin.getDataFolder(), plugin.getMainConfig().getString("warps.singlefile.name"));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Error while creating " + plugin.getMainConfig().getString("warps.singlefile.name"));
                return Optional.empty();
            }
        }
        return Optional.of(file);
    }
    
    private static Optional<FileConfig> getSingleConfig() {
        Optional<File> singleFileOpt = getSingleFile();
        if (singleFileOpt.isEmpty()) {
            return Optional.empty();
        }
        
        FileConfig config;
        try {
            config = YamlConfig.loadConfiguration(singleFileOpt.get());
        } catch (Throwable t) {
            return Optional.empty();
        }
        
        return Optional.of(config);
    }
    
    private static Optional<File> getWarpsFolder() {
        if (!"separatefiles".equalsIgnoreCase(plugin.getMainConfig().getString("warps.storage"))) {
            return Optional.empty();
        }
        
        File folder = new File(plugin.getDataFolder(), plugin.getMainConfig().getString("warps.separatefiles.folder"));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        return Optional.of(folder);
    }
    
    /**
     * Loads all warps from storage
     *
     * @throws IllegalArgumentException If the storage mode from the config if it is invalid
     */
    public static void loadWarps() {
        String storageMode = plugin.getMainConfig().getString("warps.storage");
        if ("singlefile".equalsIgnoreCase(storageMode)) {
            Optional<FileConfig> singleConfigOpt = getSingleConfig();
            if (singleConfigOpt.isEmpty()) {
                return;
            }
            
            FileConfig config = singleConfigOpt.get();
            for (String name : config.getKeys()) {
                Section warpSection = config.getSection(name);
                if (warpSection == null) {
                    continue;
                }
                
                Map<String, Warp> warps = loadWarps(warpSection);
                StarWarps.warps.putAll(warps);
            }
        } else if (storageMode.equalsIgnoreCase("separatefiles")) {
            Optional<File> warpsFolderOpt = getWarpsFolder();
            if (warpsFolderOpt.isEmpty()) {
                return;
            }
            
            File folder = warpsFolderOpt.get();
            
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    continue;
                }
                
                String fileName = file.getName();
                if (!fileName.endsWith(".yml")) {
                    continue;
                }
                
                int periodIndex = fileName.lastIndexOf('.');
                
                FileConfig config = YamlConfig.loadConfiguration(file);
                Map<String, Warp> warps = loadWarps(config);
                StarWarps.warps.putAll(warps);
            }
            
        } else {
            throw new IllegalArgumentException("Invalid storage mode provided");
        }
    }
    
    /**
     * Loads all warps for a section
     *
     * @param section The config section that contains the warps
     * @return The Map of all warps loaded from the section
     */
    public static Map<String, Warp> loadWarps(Section section) {
        Map<String, Warp> warps = new HashMap<>();
        for (String name : section.getKeys()) {
            Actor owner = Actors.create(section.getString(name + ".owner"));
            double x = section.getDouble(name + ".x");
            double y = section.getDouble(name + ".y");
            double z = section.getDouble(name + ".z");
            float yaw = (float) section.getDouble(name + ".yaw");
            float pitch = (float) section.getDouble(name + ".pitch");
            Position position = new Position(x, y, z, yaw, pitch);
            String worldName = section.getString(name + ".worldName");
            String permission = section.getString(name + ".position");
            warps.put(name.toLowerCase(), new Warp(owner, name.toLowerCase(), worldName, position, permission));
        }
        
        return warps;
    }
    
    /**
     * Saves all warps to storage
     *
     * @throws IllegalArgumentException If the storage mode from the config is invalid
     */
    public static void saveWarp() {
        try {
            String storageMode = plugin.getMainConfig().getString("warps.storage");
            if (storageMode.equalsIgnoreCase("singlefile")) {
                Optional<FileConfig> singleConfigOpt = getSingleConfig();
                if (singleConfigOpt.isEmpty()) {
                    return;
                }
                
                FileConfig config = singleConfigOpt.get();
                for (String name : config.getKeys()) {
                    config.set(name, null);
                }
                
                config.save();
                
                warps.forEach((name, warp) -> {
                    Section section;
                    if (!config.contains(name)) {
                        section = config.createSection(name);
                    } else {
                        section = config.getSection(name);
                    }
                    
                    saveWarp(warp, section);
                });
                
                config.save();
            } else if (storageMode.equalsIgnoreCase("separatefiles")) {
                Optional<File> warpsFolderOpt = getWarpsFolder();
                if (warpsFolderOpt.isEmpty()) {
                    return;
                }
                
                File folder = warpsFolderOpt.get();
                
                warps.forEach((name, warp) -> {
                    File file = new File(folder, name + ".yml");
                    if (file.exists()) {
                        file.delete();
                    }
                    
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        plugin.getLogger().severe("Could not create the file " + file.getName());
                        return;
                    }
                    
                    FileConfig config = YamlConfig.loadConfiguration(file);
                    saveWarp(warp, config);
                    config.save();
                });
            } else {
                throw new IllegalArgumentException("Invalid storage mode provided");
            }
        } catch (ConcurrentModificationException e) {
        }
    }
    
    /**
     * Saves all warps in a collection in the section provided
     *
     * @param warp   The warp to save
     * @param section The section to save the warp in
     */
    public static void saveWarp(Warp warp, Section section) {
            String name = warp.getName();
            Position position = warp.getPosition();
            section.set(name + ".x", position.getX());
            section.set(name + ".y", position.getY());
            section.set(name + ".z", position.getZ());
            section.set(name + ".yaw", position.getYaw());
            section.set(name + ".pitch", position.getPitch());
            section.set(name + ".worldName", warp.getWorldName());
            section.set(name + ".permission", warp.getPermission());
            section.set(name + ".owner", warp.getOwner().getConfigString());
    }
    
    /**
     * Gets a mapping of all warps
     *
     * @return The ObservableMap of warp names to warps
     */
    public static ObservableMap<String, Warp> getWarps() {
        return warps;
    }
    
    /**
     * Gets a warp based on an owner and a name
     *
     * @param name  The name (case-insensitive)
     * @return An optional that is null if a warp does not exist, or the warp if found
     */
    public static Optional<Warp> getWarp(String name) {
        for (Warp warp : new ArrayList<>(warps.values())) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return Optional.of(warp);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Status enum for the Set Warp actions
     */
    public enum SetWarpStatus {
        /**
         * The setting of the warp was successful
         */
        SUCCESS,
        
        /**
         * The {@link SetWarpEvent} was cancelled
         */
        EVENT_CANCELLED
    }
    
    /**
     * Record for the return info when using set warp
     *
     * @param warp   The warp that is returned. This is never null
     * @param status The status of the action
     */
    public record SetWarpInfo(Warp warp, SetWarpStatus status) {
    }
    
    /**
     * Sets a warp with provided information.
     *
     * @param owner    The owner of the warp
     * @param name     The name for the warp
     * @param location The location where the warp is set
     * @return The information regarding the action
     */
    public static SetWarpInfo setWarp(Actor owner, String name, Location location, String permission) {
        return setWarp(owner, name, location, permission, null);
    }
    
    /**
     * Sets a warp with provided information
     *
     * @param owner    The owner of the warp
     * @param name     The name for the warp
     * @param location The location where the warp is set
     * @param actor    The actor that performed the action (Can be null)
     * @return The information regarding the action
     */
    public static SetWarpInfo setWarp(Actor owner, String name, Location location, String permission, Actor actor) {
        Warp warp = null;
        boolean add = false;
        
        if (warps.isEmpty()) {
            warp = new Warp(owner, name, location, permission);
            add = true;
        }
        
        if (warp == null) {
            for (Warp w : new ArrayList<>(warps.values())) {
                if (w.getName().equalsIgnoreCase(name)) {
                    w.setLocation(location);
                    warp = w;
                    break;
                }
            }
        }
        
        if (warp == null) {
            warp = new Warp(owner, name, location, permission);
            add = true;
        }
        
        SetWarpEvent setWarpEvent = new SetWarpEvent(warp, actor);
        Bukkit.getPluginManager().callEvent(setWarpEvent);
        
        if (setWarpEvent.isCancelled()) {
            return new SetWarpInfo(warp, SetWarpStatus.EVENT_CANCELLED);
        }
        
        if (add) {
            warps.put(warp.getName(), warp);
        }
        
        return new SetWarpInfo(warp, SetWarpStatus.SUCCESS);
    }
    
    /**
     * Status enum for delete warp actions
     */
    public enum DeleteWarpStatus {
        /**
         * The deletion of the warp was successful
         */
        SUCCESS,
        
        /**
         * The {@link DeleteWarpEvent} was cancelled
         */
        EVENT_CANCELLED,
        
        /**
         * No warp was found with the name provided
         */
        NO_WARP
    }
    
    /**
     * Record for the delete warp action information
     *
     * @param warp   The warp optional. This only exists if the Status is SUCCESS
     * @param name   The name of the warp to be deleted or was deleted
     * @param status The status of the action
     */
    public record DeleteWarpInfo(Optional<Warp> warp, String name, DeleteWarpStatus status) {
    }
    
    /**
     * Deletes a warp based on provided values
     *
     * @param name  The name of the warp (case-insensitive)
     * @return The action information
     */
    public static DeleteWarpInfo deleteWarp(String name) {
        return deleteWarp(name, null);
    }
    
    /**
     * Deletes a warp based on provided values
     *
     * @param name  The name of the warp (case-insensitive)
     * @param actor The Actor that performed the action (can be null)
     * @return The action information
     */
    public static DeleteWarpInfo deleteWarp(String name, Actor actor) {
        if (warps.isEmpty()) {
            return new DeleteWarpInfo(Optional.empty(), name, DeleteWarpStatus.NO_WARP);
        }
        
        Iterator<Warp> iterator = warps.values().iterator();
        while (iterator.hasNext()) {
            Warp warp = iterator.next();
            if (warp.getName().equalsIgnoreCase(name)) {
                DeleteWarpEvent deleteWarpEvent = new DeleteWarpEvent(warp, actor);
                Bukkit.getPluginManager().callEvent(deleteWarpEvent);
                
                if (deleteWarpEvent.isCancelled()) {
                    return new DeleteWarpInfo(Optional.of(warp), name, DeleteWarpStatus.EVENT_CANCELLED);
                }
                
                iterator.remove();
                return new DeleteWarpInfo(Optional.of(warp), name, DeleteWarpStatus.SUCCESS);
            }
        }
        
        return new DeleteWarpInfo(Optional.empty(), name, DeleteWarpStatus.NO_WARP);
    }
    
    /**
     * Status enum for rename actions
     */
    public enum RenameWarpStatus {
        /**
         * The rename was successful
         */
        SUCCESS,
        
        /**
         * The {@link RenameWarpEvent} was cancelled
         */
        EVENT_CANCELLED,
        
        /**
         * No warp was found with the provided name
         */
        NO_WARP
    }
    
    /**
     * Record for the information relatled to renaming a warp
     *
     * @param warp    The warp that was renamed (This is present if the event cancelled and success status are the ones)
     * @param oldName The old name (This may not be the direct name provided, it is replaced with the actual name if a warp is found)
     * @param newName The new name
     * @param status  The status of the action
     */
    public record RenameWarpInfo(Optional<Warp> warp, String oldName, String newName, RenameWarpStatus status) {
    }
    
    /**
     * Renames a warp with provided information
     *
     * @param oldName The old name of the warp (This might get changed in the info if a warp was found and differs in case)
     * @param newName The new name for the warp
     * @param actor   The actor the performed the action
     * @return The information related to the action
     */
    public static RenameWarpInfo renameWarp(String oldName, String newName, Actor actor) {
        if (warps.isEmpty()) {
            return new RenameWarpInfo(Optional.empty(), oldName, newName, RenameWarpStatus.NO_WARP);
        }
        
        for (Warp warp : new ArrayList<>(warps.values())) {
            if (warp.getName().equalsIgnoreCase(oldName)) {
                String existingName = warp.getName();
                
                RenameWarpEvent renameWarpEvent = new RenameWarpEvent(warp, newName, actor);
                Bukkit.getPluginManager().callEvent(renameWarpEvent);
                
                if (renameWarpEvent.isCancelled()) {
                    return new RenameWarpInfo(Optional.of(warp), existingName, newName, RenameWarpStatus.EVENT_CANCELLED);
                }
                
                warp.setName(newName);
                return new RenameWarpInfo(Optional.of(warp), existingName, newName, RenameWarpStatus.SUCCESS);
            }
        }
        
        return new RenameWarpInfo(Optional.empty(), oldName, newName, RenameWarpStatus.NO_WARP);
    }
    
    /**
     * Renames a warp with provided information
     *
     * @param oldName The old name of the warp (This might get changed in the info if a warp was found and differs in case)
     * @param newName The new name for the warp
     * @return Th
     * e information related to the action
     */
    public static RenameWarpInfo renameWarp(String oldName, String newName) {
        return renameWarp(oldName, newName, null);
    }
}