package work.novablog.mcplugin.mcweb.record;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.querz.nbt.io.*;
import net.querz.nbt.tag.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.Nullable;
import work.novablog.mcplugin.mcweb.MCWeb;
import work.novablog.mcplugin.mcweb.SchematicWriter;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class RecordingWriter {
    private final Region region;
    private final long startTicks;
    private final CompoundTag data;
    private final CompoundTag eventsData;

    public RecordingWriter(Region region, long startTicks) {
        this.region = region;
        this.startTicks = startTicks;
        data = new CompoundTag();
        eventsData = new CompoundTag();

        data.put("schem", getSchematicCompoundTag());
    }

    private @Nullable Tag<?> getSchematicCompoundTag() {
        var outputStream = new ByteArrayOutputStream();
        NamedTag schematicTag;
        try {
            SchematicWriter.write(region, outputStream);
            var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            schematicTag = new NBTDeserializer(true).fromStream(inputStream);
        } catch (WorldEditException | IOException e) {
            e.printStackTrace();
            return null;
        }

        return schematicTag.getTag();
    }

    /**
     * 録画ファイルを保存する
     */
    public void save() throws IOException {
        data.put("events", eventsData);

        File file = new File(MCWeb.getInstance().getDataFolder(), "test.mcsr");
        try (var nbtOut = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(file), true))) {
            nbtOut.writeTag(new NamedTag(null, data), Tag.DEFAULT_MAX_DEPTH);
        }
    }

    /**
     * イベントに関するデータを変数に保存する
     * @param events 書き込むイベント
     * @param elapsedTicks 経過したティック数
     */
    public void saveEvents(List<Cancellable> events, long elapsedTicks) {
        var tickData = new TickData();

        events.forEach(event -> {
            try {
                writeEventToTickData(event, tickData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

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

        if(tickDataCompoundTag.size() == 0) return;

        eventsData.put(String.valueOf(elapsedTicks - startTicks), tickDataCompoundTag);
    }

    private void writeEventToTickData(Cancellable event, TickData tickData) throws Exception {
        var eventData = new CompoundTag();

        if(event instanceof BlockPlaceEvent) {
            var block = ((BlockPlaceEvent) event).getBlock();
            if (!isInRegion(block.getLocation())) return;
            var minPos = region.getMinimumPoint();
            var pos = block.getLocation().subtract(minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ());

            var blockData = WrappedBlockData.createData(block.getBlockData()).getHandle();
            eventData.put("BlockId", new IntTag(getBlockStateId(blockData)));
            eventData.put("Pos", locToListTag(pos));
            tickData.block.add(eventData);
        }else if(event instanceof BlockBreakEvent){
            var block = ((BlockBreakEvent) event).getBlock();
            if (!isInRegion(block.getLocation())) return;
            var minPos = region.getMinimumPoint();
            var pos = block.getLocation().subtract(minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ());

            eventData.put("BlockId", new IntTag(0));
            eventData.put("Pos", locToListTag(pos));
            tickData.block.add(eventData);
        }else{
            MCWeb.getInstance().getLogger().info(
                    event.getClass().getName() +" handling method is not implemented."
            );
        }
    }

    private int getBlockStateId(Object blockData) throws Exception {
        var netMinecraftBlockClass = Class.forName("net.minecraft.world.level.block.Block");
        var netMinecraftIBlockDataClass = Class.forName("net.minecraft.world.level.block.state.IBlockData");
        Method getStateIdMethod = netMinecraftBlockClass.getMethod("i", netMinecraftIBlockDataClass);

        return (int) getStateIdMethod.invoke(null, blockData);
    }

    private ListTag<DoubleTag> locToListTag(Location loc) {
        var tag = new ListTag<>(DoubleTag.class);
        tag.addDouble(loc.getX());
        tag.addDouble(loc.getY());
        tag.addDouble(loc.getZ());
        return tag;
    }

    private static class TickData {
        public final ListTag<CompoundTag> block = new ListTag<>(CompoundTag.class);
    }

    /**
     * 座標がRegion範囲内かどうか
     * @param loc 座標
     * @return 範囲内ならtrue
     */
    private boolean isInRegion(Location loc) {
        var locWorld = new BukkitWorld(loc.getWorld());
        return locWorld.equals(region.getWorld()) && region.contains(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }
}
