package work.novablog.mcplugin.mcweb;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

public class SchematicWriter {
    /**
     * OutputStreamにSchematicを書き出す
     * @param region 書き出す地域
     * @param outputStream 書き出し先
     */
    public static void write(@NotNull Region region, @NotNull OutputStream outputStream) throws WorldEditException, IOException {
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                Objects.requireNonNull(region.getWorld()), region, clipboard, region.getMinimumPoint()
        );
        forwardExtentCopy.setCopyingEntities(true);
        Operations.complete(forwardExtentCopy);

        try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(outputStream)){
            clipboardWriter.write(clipboard);
        }
    }
}
