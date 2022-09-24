package com.github.nova_27.mcplugin.crafterepost.command;

import com.github.nova_27.mcplugin.crafterepost.CrafterePost;
import com.github.nova_27.mcplugin.crafterepost.Utils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecordCommand extends BaseCommand {

    @Override
    public String getName() {
        return "record";
    }

    @Override
    public boolean checkPermission(Player player) {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行可能です！");
            return;
        }

        var recordingManager = CrafterePost.getInstance().getRecordingManager();
        var uuid = player.getUniqueId();

        if (recordingManager.stopRecording(uuid)) {
            player.sendMessage(ChatColor.BLUE + "録画停止");
            return;
        }

        Region region;
        try {
            region = Utils.getSelection(player);
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.RED + "wandで保存範囲を選択してください！");
            return;
        }

        recordingManager.startRecording(uuid, region);
        player.sendMessage(ChatColor.BLUE + "録画開始");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
