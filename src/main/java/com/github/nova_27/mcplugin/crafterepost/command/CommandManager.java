package com.github.nova_27.mcplugin.crafterepost.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, BaseCommand> commands;

    public CommandManager() {
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // サブコマンドが未指定
        if(args.length == 0) return false;

        var subCommandArgs = args.length < 2 ? new String[]{} : Arrays.copyOfRange(args, 1, args.length);
        var subCommand = commands.get(args[0]);

        // サブコマンドが存在しない
        if(subCommand == null) return false;

        if(sender instanceof Player player && !subCommand.checkPermission(player)) {
            //権限無し
            player.sendMessage(ChatColor.RED + "権限が不足しています！");
            return true;
        }

        subCommand.onCommand(sender, subCommandArgs);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    public void register(BaseCommand command) {
        commands.put(command.getName(), command);
    }
}
