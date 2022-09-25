package com.github.nova_27.mcplugin.crafterepost.command;

import com.github.nova_27.mcplugin.crafterepost.CrafterePost;
import com.github.nova_27.mcplugin.crafterepost.Utils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordCommand extends BaseCommand {
    private static final List<String> commands = new ArrayList<>() {
        {
            add("start");
            add("stop");
        }
    };

    @Override
    public String getName() {
        return "record";
    }

    @Override
    public boolean checkPermission(Player player) {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行可能です！");
            return;
        }

        if (args.length < 1) args = new String[]{""};

        var recordingManager = CrafterePost.getInstance().getRecordingManager();
        var uuid = player.getUniqueId();

        switch (args[0]) {
            case "start":
                Region region;
                try {
                    region = Utils.getSelection(player);
                } catch (IncompleteRegionException e) {
                    player.sendMessage(ChatColor.RED + "wandで保存範囲を選択してください！");
                    return;
                }

                args = Arrays.copyOfRange(args, 1, args.length);

                File file;
                try {
                    file = Utils.createFileInstanceFromArgs(args, "mcsr");
                } catch (IOException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                    return;
                }

                if (recordingManager.startRecording(uuid, region, file)) {
                    player.sendMessage(ChatColor.BLUE + "録画を開始しました");
                } else {
                    player.sendMessage(ChatColor.RED + "録画は既に開始されています！");
                }
                break;
            case "stop":
                if (recordingManager.stopRecording(uuid)) {
                    player.sendMessage(ChatColor.BLUE + "録画を停止しました");
                } else {
                    player.sendMessage(ChatColor.RED + "録画は開始していません！");
                }
                break;
            default:
                player.sendMessage("usage: /<command> record start [fileName] | /<command> record stop".replace("<command>", label));
                break;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) || args.length > 1) return null;
        return commands.stream().filter(cmd -> cmd.startsWith(args[0])).toList();
    }
}
