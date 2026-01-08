package com.stardevllc.starwarps.plugin;

import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.commands.*;
import com.stardevllc.starmclib.StarMCLib;
import com.stardevllc.starmclib.plugin.ExtendedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class StandaloneStarWarpsPlugin extends ExtendedJavaPlugin {
    @Override
    public void onEnable() {
        Plugin starmclibPlugin = Bukkit.getPluginManager().getPlugin("StarMCLib");
        if (starmclibPlugin != null) {
            getLogger().severe("StarMCLib plugin detected with the StarWarps-Standalone plugin");
            getLogger().severe("Please either replace StarWarps-Standalone with StarWarps Plugin or remove StarMCLib plugin");
            getLogger().severe("Please see the wiki page for more information");
            getLogger().severe("https://github.com/StarDevelopmentLLC/StarWarps/wiki/Available-Binaries");
        }
        
        Plugin starcorePlugin = Bukkit.getPluginManager().getPlugin("StarCore");
        if (starcorePlugin != null) {
            getLogger().severe("StarCore plugin detected with the StarWarps-Standalone plugin");
            getLogger().severe("Please either replace StarWarps-Standalone with StarWarps Plugin or remove StarCore");
            getLogger().severe("Please see the wiki page for more information");
            getLogger().severe("https://github.com/StarDevelopmentLLC/StarWarps/wiki/Available-Binaries");
        }
        
        StarMCLib.init(this);
        super.onEnable();
        StarWarps.init(this);
        registerCommand(new WarpCommand(this), new DelWarpCommand(this), new RenameWarpCommand(this), new SetWarpCommand(this));
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        StarWarps.saveWarp();
    }
}
