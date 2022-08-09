package work.novablog.mcplugin.mcweb.record;

import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BukkitEventListener implements Listener {
    private final List<Cancellable> receivedEvents;

    public BukkitEventListener() {
        receivedEvents = new ArrayList<>();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        receivedEvents.add(e);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        receivedEvents.add(e);
    }

    public List<Cancellable> getReceivedEvents() {
        return receivedEvents;
    }

    public void clearReceivedEvents() {
        receivedEvents.clear();
    }
}
