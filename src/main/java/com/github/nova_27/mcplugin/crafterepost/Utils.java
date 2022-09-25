package com.github.nova_27.mcplugin.crafterepost;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class Utils {
    /**
     * wand選択範囲を取得する
     *
     * @param player 対象プレイヤー
     * @return 選択範囲
     * @throws IncompleteRegionException 範囲未選択の場合
     */
    public static Region getSelection(Player player) throws IncompleteRegionException {
        BukkitPlayer actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);

        return localSession.getSelection();
    }

    /**
     * OutputStreamにSchematicを書き出す
     *
     * @param region       書き出す地域
     * @param outputStream 書き出し先
     */
    public static void writeSchematic(@NotNull Region region, @NotNull OutputStream outputStream) throws WorldEditException, IOException {
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                Objects.requireNonNull(region.getWorld()), region, clipboard, region.getMinimumPoint()
        );
        //forwardExtentCopy.setCopyingEntities(true);
        Operations.complete(forwardExtentCopy);

        try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(outputStream)) {
            clipboardWriter.write(clipboard);
        }
    }

    /**
     * 有効なファイル名かどうか
     *
     * @param fileName 検証するファイル名
     * @return trueなら有効
     */
    public static boolean isValidFileName(String fileName) {
        return !fileName.matches("^.*[\\\\|/|:|\\*|?|\"|<|>|\\|].*$");
    }
}
