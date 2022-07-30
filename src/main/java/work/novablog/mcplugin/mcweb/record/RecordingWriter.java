package work.novablog.mcplugin.mcweb.record;

import com.sk89q.worldedit.regions.Region;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
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
}
