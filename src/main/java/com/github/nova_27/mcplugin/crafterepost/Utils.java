package com.github.nova_27.mcplugin.crafterepost;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class Utils {
    private static final String GITHUB_LATEST = "https://api.github.com/repos/nova-27/CrafterePostPlugin/releases/latest";

    /**
     * プラグインの最新バージョン番号を取得する
     *
     * @return バージョン番号 確認に失敗したらnull
     */
    public static @Nullable String getLatestVersion() {
        try {
            var result = new StringBuilder();

            var url = new URL(GITHUB_LATEST);
            var con = (HttpURLConnection) url.openConnection();
            con.connect();
            var in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) result.append(line);

            in.close();
            con.disconnect();

            var mapper = new ObjectMapper();
            var root = mapper.readTree(result.toString());

            return root.get("tag_name").asText().replace("v", "");
        } catch (Exception e) {
            return null;
        }
    }

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
     * BukkitBlockDataからブロック状態キーを取得する
     *
     * @param blockData BukkitBlockData
     * @return ブロック状態キー
     */
    public static String getBlockKey(BlockData blockData) {
        var blockState = BukkitAdapter.adapt(blockData);
        return blockState.toBaseBlock().toImmutableState().getAsString();
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

    /**
     * コマンド引数からFileインスタンスを生成する
     *
     * @param args コマンド引数
     * @param ext  拡張子
     * @return 生成したFile
     * @throws IOException エラーが発生したらメッセージ付きでthrow
     */
    public static File createFileInstanceFromArgs(String[] args, String ext) throws IOException {
        var doOverwrite = false;
        var fileName = "";

        for (var arg : args) {
            if (arg.startsWith("-")) {
                // フラグだったら
                if (arg.equalsIgnoreCase("-f")) {
                    doOverwrite = true;
                } else {
                    throw new IOException("無効なフラグ: " + arg);
                }
            } else {
                //ファイル名なら
                fileName = arg;
            }
        }

        if (fileName.equals("")) throw new IOException("保存ファイル名を指定してください");

        fileName += "." + ext;
        if (!isValidFileName(fileName)) throw new IOException("ファイル名に禁則文字が含まれています");

        var file = new File(CrafterePost.getInstance().getDataFolder(), fileName);
        if (!doOverwrite && file.exists()) throw new IOException("ファイルが存在します\n上書きするには -f フラグを使用してください");

        if (!file.createNewFile() && !file.canWrite()) throw new IOException("ファイルを書き出すことができません");

        return file;
    }
}
