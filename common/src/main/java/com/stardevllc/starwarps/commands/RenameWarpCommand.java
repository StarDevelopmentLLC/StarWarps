package com.stardevllc.starwarps.commands;

import com.stardevllc.starmclib.actors.Actors;
import com.stardevllc.starmclib.command.flags.FlagResult;
import com.stardevllc.starmclib.plugin.ExtendedJavaPlugin;
import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.StarWarps.RenameWarpInfo;
import com.stardevllc.starwarps.StarWarps.RenameWarpStatus;
import com.stardevllc.starwarps.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RenameWarpCommand extends BaseCommand {
    public RenameWarpCommand(ExtendedJavaPlugin plugin) {
        super(plugin, "renamewarp", "Renames a warp", "starwarps.command.renamew");
        this.playerOnly = true;
        this.playerOnlyMessage = plugin.getColors().colorLegacy("&cOnly players can use that command");
    }
    
    @Override
    public boolean execute(CommandSender sender, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 1)) {
            plugin.getColors().coloredLegacy(sender, "&cYou must provide a warp name and a new name.");
            return true;
        }
        
        String newName = args[1];
        Player player = (Player) sender; 
        
        RenameWarpInfo renameWarpInfo = StarWarps.renameWarp(args[0], args[1], Actors.of(player));
        
        if (renameWarpInfo.status() == RenameWarpStatus.NO_WARP) {
            plugin.getColors().coloredLegacy(player, "&cThere is no warp named " + renameWarpInfo.oldName() + ".");
            return true;
        }
        
        if (renameWarpInfo.status() == RenameWarpStatus.EVENT_CANCELLED) {
            plugin.getColors().coloredLegacy(player, "&cRenaming the warp " + renameWarpInfo.oldName() + " was cancelled.");
            return true;
        }
        
        Optional<Warp> warpOpt = renameWarpInfo.warp();
        if (warpOpt.isEmpty()) {
            plugin.getLogger().severe("Rename Warp returned success, but the warp value was not present. Please report as a bug");
            return true;
        }
        
        Warp warp = warpOpt.get();
        
        plugin.getColors().coloredLegacy(player, "&eYou renamed the warp &b" + renameWarpInfo.oldName() + " &eto &b" + warp.getName() + "&e.");
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