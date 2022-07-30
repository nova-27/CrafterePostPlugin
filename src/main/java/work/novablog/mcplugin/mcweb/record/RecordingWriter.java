package work.novablog.mcplugin.mcweb.record;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Range;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPlaceEvent;
import work.novablog.mcplugin.mcweb.MCWeb;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
     * イベントに関するデータを変数に保存する
     * @param events 書き込むイベント
     * @param elapsedTicks 経過したティック数
     */
    public void saveEvents(List<Cancellable> events, long elapsedTicks) {
        var tickData = new TickData();

        for (var event : events) {
            writeEventToTickData(event, tickData);
        }

        var tickDataCompoundTag = new CompoundTag();
        try {
            for (var field : tickData.getClass().getFields()) {
                var fieldName = StringUtils.capitalize(field.getName());
                var fieldData = (ListTag<?>) field.get(tickData);
                if(fieldData.size() == 0) continue;
                tickDataCompoundTag.put(fieldName, fieldData);
            }
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        data.put(String.valueOf(elapsedTicks), tickDataCompoundTag);
    }

    private void writeEventToTickData(Cancellable event, TickData tickData) {
        var eventData = new CompoundTag();

        if(event instanceof BlockPlaceEvent) {
            var block = ((BlockPlaceEvent) event).getBlock();
            if(!isInRegion(block.getLocation())) return;

            eventData.put("BlockId", new StringTag(block.getBlockData().getAsString()));
            var pos = new ListTag<>(IntTag.class);
            pos.addInt(block.getX());
            pos.addInt(block.getY());
            pos.addInt(block.getZ());
            eventData.put("Pos", pos);
            tickData.blockPlace.add(eventData);
        }else{
            MCWeb.getInstance().getLogger().info(
                    event.getClass().getName() +" handling method is not implemented."
            );
        }
    }

    private static class TickData {
        public final ListTag<CompoundTag> blockPlace = new ListTag<>(CompoundTag.class);
    }

    /**
     * 座標がRegion範囲内かどうか
     * @param loc 座標
     * @return 範囲内ならtrue
     */
    private boolean isInRegion(Location loc) {
        BlockVector3 minLoc = region.getMinimumPoint();
        BlockVector3 maxLoc = region.getMaximumPoint();
        return Range.between(minLoc.getBlockX(), maxLoc.getBlockX()).contains(loc.getBlockX()) &&
                Range.between(minLoc.getBlockY(), maxLoc.getBlockY()).contains(loc.getBlockY()) &&
                Range.between(minLoc.getBlockZ(), maxLoc.getBlockZ()).contains(loc.getBlockZ());
    }
}
