package work.novablog.mcplugin.mcweb;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

public class SchematicWriter {
    public static void save(@NotNull Region region, @NotNull String fileName) throws WorldEditException, IOException {
        File file = new File(MCWeb.getInstance().getDataFolder(), fileName);

        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                Objects.requireNonNull(region.getWorld()), region, clipboard, region.getMinimumPoint()
        );
        forwardExtentCopy.setCopyingEntities(true);
        Operations.complete(forwardExtentCopy);

        var bos = new BufferedOutputStream(new FileOutputStream(file));
        ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(bos);
        clipboardWriter.write(clipboard);
        clipboardWriter.close();

        CompoundTag root = (CompoundTag) NBTUtil.read(file).getTag();

        var entitiesData = new ListTag<>(CompoundTag.class);
        for(var entity : clipboard.getEntities()) {
            var entityId = Objects.requireNonNull(entity.getState()).getType().toString();
            if(entityId.equals("item")) continue;
            var entityLocation = entity.getLocation();
            var regionMinimumPoint = region.getMinimumPoint();

            CompoundTag entityData = new CompoundTag();

            entityData.put("Id", new StringTag(entityId));
            var entityPosData = new ListTag<>(DoubleTag.class);
            entityPosData.addDouble(entityLocation.getX() - regionMinimumPoint.getX());
            entityPosData.addDouble(entityLocation.getY() - regionMinimumPoint.getY());
            entityPosData.addDouble(entityLocation.getZ() - regionMinimumPoint.getZ());
            entityData.put("Pos", entityPosData);
            var entityRotationData = new ListTag<>(FloatTag.class);
            entityRotationData.addFloat(entityLocation.getYaw());
            entityRotationData.addFloat(entityLocation.getPitch());
            entityData.put("Rotation", entityRotationData);

            entitiesData.add(entityData);
        }

        root.put("Entities", entitiesData);
        NBTUtil.write(new NamedTag("Schematic", root), file, true);
    }
}
