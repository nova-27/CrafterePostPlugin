package work.novablog.mcplugin.mcweb.record;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import work.novablog.mcplugin.mcweb.MCWeb;

import java.io.IOException;
import java.util.*;

public class RecordingManager extends BukkitRunnable implements Listener {
    private final Map<UUID, RecordingWriter> recordings;

    public RecordingManager() {
        recordings = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, MCWeb.getInstance());
    }

    /**
     * プレイヤーが録画中かどうか
     * @param uuid プレイヤーのUUID
     * @return 録画中ならtrue
     */
    public boolean isRecording(@NotNull UUID uuid) {
        return recordings.containsKey(uuid);
    }

    /**
     * 録画を開始する
     * @param uuid プレイヤーのUUID
     * @param region 録画地域
     * @return 録画中であればfalse
     */
    public boolean startRecording(@NotNull UUID uuid, @NotNull Region region) {
        if(isRecording(uuid)) return false;

        var recording = new RecordingWriter(region);
        recordings.put(uuid, recording);

        return true;
    }

    /**
     * 録画を終了する
     * @param uuid プレイヤーのUUID
     * @return 録画開始していなければfalse
     */
    public boolean stopRecording(@NotNull UUID uuid) {
        if(!isRecording(uuid)) return false;

        RecordingWriter recording = recordings.get(uuid);
        try {
            recording.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordings.remove(uuid);

        return true;
    }

    /**
     * 録画タスクを終了し、すべてのプレイヤーの録画を保存する
     */
    public void stopRecordingTask() {
        cancel();
        for(var playerUuid : recordings.keySet()) {
            stopRecording(playerUuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        stopRecording(e.getPlayer().getUniqueId());
    }

    @Override
    public void run() {
        MCWeb.getInstance().getLogger().info("テスト");
        //TODO 書き込み処理
    }
}
