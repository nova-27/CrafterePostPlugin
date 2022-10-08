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
            getLogger().log(Level.SEVERE, "Failed to create the plugin directory!");
            return;
        }

        if (!(Bukkit.getPluginManager().getPlugin("WorldEdit") instanceof WorldEditPlugin)) {
            getLogger().log(Level.SEVERE, "Could not find WorldEdit plugin!");
            return;
        }

        var commandManager = new CommandManager();
        commandManager.register(new SchemCommand());
        commandManager.register(new RecordCommand());
        Objects.requireNonNull(getCommand("crapos")).setExecutor(commandManager);
    }

    @Override
    public void onDisable() {
        recordingManager.stopRecordingTask();
    }
}