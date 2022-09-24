package com.github.nova_27.mcplugin.crafterepost.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseCommand {
    public abstract String getName();
    public abstract boolean checkPermission(Player player);
    public abstract void onCommand(CommandSender sender, String[] args);
    public abstract @Nullable List<String> onTabComplete(CommandSender sender, String[] args);
}
