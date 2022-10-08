package com.github.nova_27.mcplugin.crafterepost;

import com.github.nova_27.mcplugin.crafterepost.command.CommandManager;
import com.github.nova_27.mcplugin.crafterepost.command.RecordCommand;
import com.github.nova_27.mcplugin.crafterepost.command.SchemCommand;
import com.github.nova_27.mcplugin.crafterepost.record.RecordingManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class CrafterePost extends JavaPlugin {
    private static final int PLUGIN_ID = 16605;
    public static final String DOWNLOAD_URL = "https://github.com/nova-27/CrafterePostPlugin/releases";

    private static CrafterePost instance;
    private RecordingManager recordingManager;

    public static CrafterePost getInstance() {
        return instance;
    }

    public RecordingManager getRecordingManager() {
        return recordingManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        new Metrics(this, PLUGIN_ID);

        recordingManager = new RecordingManager();
        recordingManager.runTaskTimer(CrafterePost.getInstance(), 0L, 1L);

        if (!getDataFolder().isDirectory() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "プラグインフォルダの作成に失敗しました！");
            return;
        }

        if (!(Bukkit.getPluginManager().getPlugin("WorldEdit") instanceof WorldEditPlugin)) {
            getLogger().log(Level.SEVERE, "WorldEditプラグインが見つかりません！");
            return;
        }

        // コマンドの登録
        var commandManager = new CommandManager();
        commandManager.register(new SchemCommand());
        commandManager.register(new RecordCommand());
        Objects.requireNonNull(getCommand("crapos")).setExecutor(commandManager);

        // バージョン確認
        String currentVer = getDescription().getVersion();
        String latestVer = Utils.getLatestVersion();
        if (latestVer == null) {
            getLogger().info("最新バージョンの確認に失敗しました");
        } else if (!currentVer.equals(latestVer)) {
            getLogger().info(
                    "新しいバージョンがあります: v{current} -> v{latest}"
                            .replace("{current}", currentVer)
                            .replace("latest", latestVer)
            );
            getLogger().info("ダウンロード: {link}".replace("{link}", DOWNLOAD_URL)
            );
        }
    }

    @Override
    public void onDisable() {
        recordingManager.stopRecordingTask();
    }
}