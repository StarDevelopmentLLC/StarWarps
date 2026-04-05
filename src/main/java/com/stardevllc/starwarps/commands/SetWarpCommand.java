package com.stardevllc.starwarps.commands;

import com.stardevllc.minecraft.actors.Actors;
import com.stardevllc.minecraft.command.flags.FlagResult;
import com.stardevllc.minecraft.plugin.ExtendedJavaPlugin;
import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.StarWarps.SetWarpInfo;
import com.stardevllc.starwarps.StarWarps.SetWarpStatus;
import com.stardevllc.starwarps.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand extends BaseCommand {
    public SetWarpCommand(ExtendedJavaPlugin plugin) {
        super(plugin, "setwarp", "Sets a warp", "starwarps.command.setwarp");
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
        
        String permission = null;
        if (args.length > 1) {
            permission = args[1];
        }
        
        SetWarpInfo setWarpInfo = StarWarps.setWarp(Actors.of(player), args[0], player.getLocation(), permission, Actors.of(player));
        
        if (setWarpInfo.status() == SetWarpStatus.EVENT_CANCELLED) {
            plugin.getColors().coloredLegacy(player, "&cSetting the warp " + args[0] + " was cancelled.");
            return true;
        }
        
        Warp warp = setWarpInfo.warp();
        
        plugin.getColors().coloredLegacy(player, "&eYou set a warp named &b" + warp.getName() + "&e.");
        return true;
    }
}