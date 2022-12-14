package com.github.nova_27.mcplugin.crafterepost.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, BaseCommand> commands;

    public CommandManager() {
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // サブコマンドが未指定
        if (args.length == 0) return false;

        var subCommandArgs = args.length < 2 ? new String[]{} : Arrays.copyOfRange(args, 1, args.length);
        var subCommand = commands.get(args[0]);

        // サブコマンドが存在しない
        if (subCommand == null) return false;

        if (sender instanceof Player && !subCommand.checkPermission((Player) sender)) {
            //権限無し
            sender.sendMessage(ChatColor.RED + "権限が不足しています！");
            return true;
        }

        subCommand.onCommand(sender, label, subCommandArgs);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<BaseCommand> subCommandSuggestions = new ArrayList<>();
        for (var registeredCommand : commands.values()) {
            if (!registeredCommand.getName().startsWith(args[0])) continue;
            if (sender instanceof Player && !registeredCommand.checkPermission((Player) sender)) continue;
            subCommandSuggestions.add(registeredCommand);
        }

        if (args.length == 1)
            return subCommandSuggestions.stream().map(BaseCommand::getName).collect(Collectors.toList());
        var subCommandArgs = Arrays.copyOfRange(args, 1, args.length);

        List<String> argumentsSuggestions = new ArrayList<>();
        subCommandSuggestions.stream().map(cmd -> cmd.onTabComplete(sender, subCommandArgs)).filter(Objects::nonNull).forEach(argumentsSuggestions::addAll);

        return argumentsSuggestions;
    }

    public void register(BaseCommand command) {
        commands.put(command.getName(), command);
    }
}
