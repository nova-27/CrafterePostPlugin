package work.novablog.mcplugin.mcweb.record;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPlaceEvent;
import work.novablog.mcplugin.mcweb.MCWeb;

import java.io.File;
import java.io.IOException;

public class RecordingWriter {
    private final Region region;
    private final CompoundTag data;

    public RecordingWriter(Region region) {
        this.region = region;
        data = new CompoundTag();
    }

    /**
     * 録画ファイルを保存する
     */
    public void save() throws IOException {
        File file = new File(MCWeb.getInstance().getDataFolder(), "test.mcrec");
        NBTUtil.write(data, file);
    }

    /**
     * イベントに関するデータをNBTとして書き込む
     * @param event 書き込むイベント
     * @param elapsedTicks 経過したティック数
     */
    public void writeEvent(Cancellable event, long elapsedTicks) {
        if(event instanceof BlockPlaceEvent) {
            var block = ((BlockPlaceEvent) event).getBlock();
            if(!isInRegion(block.getLocation())) return;
            MCWeb.getInstance().getLogger().info(
                    "[" + elapsedTicks + " tick(s)] " + block.getBlockData().getAsString() + " was placed."
            );
        }else{
            MCWeb.getInstance().getLogger().info(
                    event.getClass().getName() +" handling method is not implemented."
            );
        }
    }

    /**
     * 座標がRegion範囲内かどうか
     * @param loc 座標
     * @return 範囲内ならtrue
     */
    private boolean isInRegion(Location loc) {
        BlockVector3 minLoc = region.getMinimumPoint();
        BlockVector3 maxLoc = region.getMaximumPoint();
        return isRange(loc.getBlockX(), minLoc.getBlockX(), maxLoc.getBlockX()) &&
                isRange(loc.getBlockY(), minLoc.getBlockY(), maxLoc.getBlockY()) &&
                isRange(loc.getBlockZ(), minLoc.getBlockZ(), maxLoc.getBlockZ());
    }

    public static boolean isRange(int x, int from, int to){
        return from <= x && x <= to;
    }
}
