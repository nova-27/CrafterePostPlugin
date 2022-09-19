package com.github.nova_27.mcplugin.crafterepost;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.nova_27.mcplugin.crafterepost.record.RecordingManager;

import java.util.Objects;
import java.util.logging.Level;

public class CrafterePost extends JavaPlugin {
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
        recordingManager = new RecordingManager();

        if(!getDataFolder().isDirectory() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "Failed to create the plugin directory!");
            return;
        }

        if(!(Bukkit.getPluginManager().getPlugin("WorldEdit") instanceof WorldEditPlugin)) {
            getLogger().log(Level.SEVERE, "Could not find WorldEdit plugin!");
            return;
        }

        recordingManager.runTaskTimer(CrafterePost.getInstance(), 0L, 1L);
        Objects.requireNonNull(getCommand("crapos")).setExecutor(new SpigotCommand());
    }

    @Override
    public void onDisable() {
        recordingManager.stopRecordingTask();
    }
}