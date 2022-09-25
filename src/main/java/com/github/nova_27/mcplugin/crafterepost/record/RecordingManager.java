package com.github.nova_27.mcplugin.crafterepost.record;

import com.github.nova_27.mcplugin.crafterepost.CrafterePost;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecordingManager extends BukkitRunnable implements Listener {
    private final Map<UUID, RecordingWriter> recordings;
    private final ServerEventListener serverEventListener;
    private long elapsedTicks;

    public RecordingManager() {
        recordings = new HashMap<>();
        serverEventListener = new ServerEventListener();
        elapsedTicks = 0;
        Bukkit.getServer().getPluginManager().registerEvents(this, CrafterePost.getInstance());
    }

    /**
     * プレイヤーが録画中かどうか
     *
     * @param uuid プレイヤーのUUID
     * @return 録画中ならtrue
     */
    public boolean isRecording(@NotNull UUID uuid) {
        return recordings.containsKey(uuid);
    }

    /**
     * 録画を開始する
     *
     * @param uuid       プレイヤーのUUID
     * @param region     録画地域
     * @param outputFile 出力先
     * @return 録画中であればfalse
     */
    public boolean startRecording(@NotNull UUID uuid, @NotNull Region region, File outputFile) {
        if (isRecording(uuid)) return false;

        var recording = new RecordingWriter(region, elapsedTicks, outputFile);
        recordings.put(uuid, recording);

        return true;
    }

    /**
     * 録画を終了する
     *
     * @param uuid プレイヤーのUUID
     * @return 録画開始していなければfalse
     */
    public boolean stopRecording(@NotNull UUID uuid) {
        if (!isRecording(uuid)) return false;

        RecordingWriter recording = recordings.get(uuid);
        recordings.remove(uuid);
        try {
            recording.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 録画タスクを終了し、すべてのプレイヤーの録画を保存する
     */
    public void stopRecordingTask() {
        cancel();
        for (var playerUuid : recordings.keySet()) {
            stopRecording(playerUuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        stopRecording(e.getPlayer().getUniqueId());
    }

    @Override
    public void run() {
        elapsedTicks++;

        for (var recording : recordings.values()) {
            recording.saveBukkitEvents(serverEventListener, elapsedTicks);
        }
        serverEventListener.clear();
    }
}
