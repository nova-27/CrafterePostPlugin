package com.github.nova_27.mcplugin.crafterepost.command;

import com.github.nova_27.mcplugin.crafterepost.CrafterePost;
import com.github.nova_27.mcplugin.crafterepost.Utils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SchemCommand extends BaseCommand {

    @Override
    public String getName() {
        return "schem";
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

        Region region;
        try {
            region = Utils.getSelection(player);
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.RED + "wandで保存範囲を選択してください！");
            return;
        }

        var file = new File(CrafterePost.getInstance().getDataFolder(), "test.schem");
        try (var outputStream = new FileOutputStream(file)) {
            Utils.writeSchematic(region, outputStream);
            player.sendMessage(ChatColor.GREEN + "Schematic建築ファイルを保存しました");
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "内部エラーが発生しました！");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
