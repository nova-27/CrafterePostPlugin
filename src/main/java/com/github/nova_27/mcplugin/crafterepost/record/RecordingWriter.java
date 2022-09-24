package com.github.nova_27.mcplugin.crafterepost.record;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.querz.nbt.io.*;
import net.querz.nbt.tag.*;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import com.github.nova_27.mcplugin.crafterepost.CrafterePost;
import com.github.nova_27.mcplugin.crafterepost.SchematicWriter;

import java.io.*;
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

        File file = new File(CrafterePost.getInstance().getDataFolder(), "test.mcsr");
        try (var nbtOut = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(file), true))) {
            nbtOut.writeTag(new NamedTag(null, data), Tag.DEFAULT_MAX_DEPTH);
        }
    }

    public void saveBukkitEvents(ServerEventListener listener, long elapsedTicks) {
        var tickData = new TickData();
        tickData.saveEvents("BlockChange", listener.getBlockChanges(), (loc, stateId) -> {
            if (!isInRegion(loc)) return null;
            var minPos = region.getMinimumPoint();
            var pos = loc.subtract(minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ());

            var data = new CompoundTag();
            data.put("BlockId", new IntTag(stateId));
            data.put("Pos", locToListTag(pos));
            return data;
        });

        var tickDataCompoundTag = tickData.getCompoundTag();
        if(tickDataCompoundTag.size() == 0) return;
        eventsData.put(String.valueOf(elapsedTicks - startTicks), tickDataCompoundTag);
    }

    private ListTag<DoubleTag> locToListTag(Location loc) {
        var tag = new ListTag<>(DoubleTag.class);
        tag.addDouble(loc.getX());
        tag.addDouble(loc.getY());
        tag.addDouble(loc.getZ());
        return tag;
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