package com.stardevllc.starwarps.commands;

import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.StarWarps.DeleteWarpInfo;
import com.stardevllc.starwarps.StarWarps.DeleteWarpStatus;
import com.stardevllc.starwarps.Warp;
import com.stardevllc.starmclib.actors.Actors;
import com.stardevllc.starmclib.command.flags.FlagResult;
import com.stardevllc.starmclib.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class DelWarpCommand extends BaseCommand {
    public DelWarpCommand(ExtendedJavaPlugin plugin) {
        super(plugin, "delwarp", "Deletes a warp", "starwarps.command.delwarp");
        this.playerOnly = true;
        this.playerOnlyMessage = plugin.getColors().colorLegacy("&cOnly players can use that command");
    }
    
    @Override
    public boolean execute(CommandSender sender, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            plugin.getColors().coloredLegacy(sender, "&cYou must provide a warp name.");
            return true;
        }
        
        Player player = (Player) sender;
        
        String warpName = args[0];
        DeleteWarpInfo deleteWarpInfo = StarWarps.deleteWarp(warpName, Actors.of(player));
        
        if (deleteWarpInfo.status() == DeleteWarpStatus.NO_WARP) {
            plugin.getColors().coloredLegacy(player, "&cThere is not a warp named " + warpName + ".");
            return true;
        }
        
        if (deleteWarpInfo.status() == DeleteWarpStatus.EVENT_CANCELLED) {
            plugin.getColors().coloredLegacy(player, "&cDeleting the warp " + deleteWarpInfo.name() + " was cancelled.");
            return true;
        }
        
        Optional<Warp> warpOpt = deleteWarpInfo.warp();
        
        if (warpOpt.isEmpty()) {
            plugin.getColors().coloredLegacy(player, "&cDeleting the warp " + deleteWarpInfo.name() + " failed. Please report to the plugin author as a bug.");
            return true;
        }
        
        Warp warp = warpOpt.get();
        
        plugin.getColors().coloredLegacy(player, "&eYou deleted the warp &b" + warp.getName() + "&e.");
        return true;
    }
    
    @Override
    public List<String> getCompletions(CommandSender sender, String label, String[] args, FlagResult flagResults) {
        if (args.length != 1) {
            return List.of();
        }
        
        List<String> completions = new ArrayList<>();
        Player player = (Player) sender;
        for (Warp warp : StarWarps.getWarps().values()) {
            if (warp.getPermission() != null && !warp.getPermission().isBlank()) {
                if (!sender.hasPermission(warp.getPermission())) {
                    continue;
                }
            }
            completions.add(warp.getName().toLowerCase());
        }
        
        String arg = args[0].toLowerCase();
        completions.removeIf(c -> !c.startsWith(arg));
        
        return completions;
    }
}