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
            player.sendMessage(ChatColor.RED + "wandで保存範囲を選択してください");
            return;
        }

        File file;
        try {
            file = createFileInstanceFromArgs(args);
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
            return;
        }

        try (var outputStream = new FileOutputStream(file)) {
            Utils.writeSchematic(region, outputStream);
            player.sendMessage(ChatColor.GREEN + "Schematic建築ファイルを保存しました");
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "内部エラーが発生しました");
        }
    }

    private File createFileInstanceFromArgs(String[] args) throws IOException {
        var doOverwrite = false;
        var fileName = "";

        for (var arg : args) {
            if (arg.startsWith("-")) {
                // フラグだったら
                if (arg.equalsIgnoreCase("-f")) {
                    doOverwrite = true;
                } else {
                    throw new IOException("無効なフラグ: " + arg);
                }
            } else {
                //ファイル名なら
                fileName = arg;
            }
        }

        if (fileName.equals("")) throw new IOException("保存ファイル名を指定してください");

        fileName += ".schem";
        if (!Utils.isValidFileName(fileName)) throw new IOException("ファイル名に禁則文字が含まれています");

        var file = new File(CrafterePost.getInstance().getDataFolder(), fileName);
        if (!doOverwrite && file.exists()) throw new IOException("ファイルが存在します\n上書きするには -f フラグを使用してください");

        if (!file.createNewFile() && !file.canWrite()) throw new IOException("ファイルを書き出すことができません");

        return file;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
