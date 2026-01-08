package com.stardevllc.starwarps.plugin;

import com.stardevllc.starwarps.StarWarps;
import com.stardevllc.starwarps.commands.*;
import com.stardevllc.starmclib.plugin.ExtendedJavaPlugin;

public class StarWarpsPlugin extends ExtendedJavaPlugin {
    @Override
    public void onEnable() {
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