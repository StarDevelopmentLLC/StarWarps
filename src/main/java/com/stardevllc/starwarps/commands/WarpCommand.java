package com.stardevllc.starwarps.commands;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.stardevllc.starmclib.plugin.ExtendedJavaPlugin;
import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class WarpCommand extends BaseCommand {
    public WarpCommand(ExtendedJavaPlugin plugin) {
        super(plugin, "warp", "Teleports a warp", "starwarp.command.warp");
        this.playerOnly = true;
        this.playerOnlyMessage = plugin.getColors().colorLegacy("&cOnly players can use that command");
    }
    
    @Override
    public boolean execute(CommandSender sender, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        if (args.length == 0) {
            Collection<Warp> warps = new ArrayList<>(StarWarps.getWarps().values());
            StringBuilder sb = new StringBuilder();
            for (Warp warp : warps) {
                if (warp.getPermission() != null && !warp.getPermission().isBlank()) {
                    if (!player.hasPermission(warp.getPermission())) {
                        continue;
                    }
                }
                
                sb.append("&b").append(warp.getName().toLowerCase()).append(" ");
            }
            
            plugin.getColors().coloredLegacy(player, "&eWarps: " + sb.toString().trim());
            return true;
        }
        
        String warpName = args[0];
        Optional<Warp> warpOpt = StarWarps.getWarp(warpName);
        
        if (warpOpt.isEmpty()) {
            plugin.getColors().coloredLegacy(player, "&cA warp named " + warpName + " does not exist.");
            return true;
        }
        
        Warp warp = warpOpt.get();
        
        if (warp.getPermission() != null && !warp.getPermission().isBlank()) {
            if (!player.hasPermission(warp.getPermission())) {
                plugin.getColors().coloredLegacy(player, "&cYou do not have permission to teleport to the warp " + warp.getName());
                return true;
            }
        }
        
        warp.teleport(player);
        
        plugin.getColors().coloredLegacy(player, "&eYou teleported to the warp named &b" + warp.getName() + "&e.");
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