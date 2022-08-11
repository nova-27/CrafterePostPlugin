package work.novablog.mcplugin.mcweb.record;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BukkitEventListener implements Listener {
    private final Map<Location, Integer> blockChanges;

    public BukkitEventListener() {
        blockChanges = new HashMap<>();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        var block = e.getBlockPlaced();
        var blockData = WrappedBlockData.createData(block.getBlockData());
        blockChanges.put(block.getLocation(), getBlockStateId(blockData));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        blockChanges.put(e.getBlock().getLocation(), 0);
    }

    public Map<Location, Integer> getBlockChanges() {
        return blockChanges;
    }

    public void clear() {
        blockChanges.clear();
    }

    private static int getBlockStateId(WrappedBlockData blockData) {
        var id = 0;
        try {
            var netMinecraftBlockClass = Class.forName("net.minecraft.world.level.block.Block");
            var netMinecraftIBlockDataClass = Class.forName("net.minecraft.world.level.block.state.IBlockData");
            Method getStateIdMethod = netMinecraftBlockClass.getMethod("i", netMinecraftIBlockDataClass);

            id = (int) getStateIdMethod.invoke(null, blockData.getHandle());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}
