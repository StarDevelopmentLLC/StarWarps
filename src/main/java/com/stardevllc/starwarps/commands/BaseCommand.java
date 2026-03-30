package com.stardevllc.starwarps.commands;

import com.stardevllc.command.StarCommand;
import com.stardevllc.plugin.ExtendedJavaPlugin;

public abstract class BaseCommand extends StarCommand<ExtendedJavaPlugin> {
    public BaseCommand(ExtendedJavaPlugin plugin, String name, String description, String permission, String... aliases) {
        super(plugin, name, description, permission, aliases);
    }
}
