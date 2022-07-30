package work.novablog.mcplugin.mcweb.record;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import work.novablog.mcplugin.mcweb.MCWeb;

import java.util.*;

public class RecordingManager extends BukkitRunnable {
    private final Map<UUID, RecordingWriter> recordings;

    public RecordingManager() {
        recordings = new HashMap<>();
    }

    /**
     * プレイヤーが録画中かどうか
     * @param player org.bukkit.entity.Player
     * @return 録画中ならtrue
     */
    public boolean isRecording(@NotNull Player player) {
        return recordings.containsKey(player.getUniqueId());
    }

    /**
     * 録画を開始する
     * @param player org.bukkit.entity.Player
     * @param region 録画地域
     * @return 録画開始できればtrue
     */
    public boolean startRecording(@NotNull Player player, @NotNull Region region) {
        if(isRecording(player)) return false;

        var recording = new RecordingWriter();
        recordings.put(player.getUniqueId(), recording);
        return true;
    }

    /**
     * 録画を終了する
     * @param player bukkitPlayer
     * @return 録画停止できればtrue
     */
    public boolean stopRecording(@NotNull Player player) {
        if(!isRecording(player)) return false;

        recordings.remove(player.getUniqueId());
        //TODO 保存処理
        return true;
    }

    /**
     * 録画タスクを終了し、すべてのプレイヤーの録画を保存する
     */
    public void stopRecordingTask() {
        cancel();
        //TODO 保存処理
    }

    @Override
    public void run() {
        MCWeb.getInstance().getLogger().info("テスト");
        //TODO 書き込み処理
    }
}
