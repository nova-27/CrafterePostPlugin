package com.github.nova_27.mcplugin.crafterepost.record;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.github.nova_27.mcplugin.crafterepost.CrafterePost;
import com.github.nova_27.mcplugin.crafterepost.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerEventListener implements Listener {
    private final Map<Location, String> blockChanges;
    private final Map<UUID, Location> playerMove;

    public ServerEventListener() {
        blockChanges = new HashMap<>();
        playerMove = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, CrafterePost.getInstance());
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                CrafterePost.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Server.BLOCK_CHANGE,
                PacketType.Play.Server.MULTI_BLOCK_CHANGE
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                if (packet.getType() == PacketType.Play.Server.BLOCK_CHANGE) {
                    var loc = packet.getBlockPositionModifier().read(0).toLocation(event.getPlayer().getWorld());
                    var blockData = loc.getBlock().getBlockData();
                    blockChanges.put(loc, Utils.getBlockKey(blockData));
                } else if (packet.getType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
                    var sectionPos = packet.getSectionPositions().read(0);
                    var shortLocations = packet.getShortArrays().read(0);

                    for (short shortLocation : shortLocations) {
                        var loc = convertShortLocation(event.getPlayer().getWorld(), sectionPos, shortLocation);
                        var blockData = loc.getBlock().getBlockData();
                        blockChanges.put(loc, Utils.getBlockKey(blockData));
                    }
                }
            }
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        playerMove.put(e.getPlayer().getUniqueId(), e.getTo());
    }

    private static Location convertShortLocation(World world, BlockPosition sectionPosition, short shortLoc) {
        int y = (sectionPosition.getY() * 16) + (shortLoc & 0xF);
        int z = (sectionPosition.getZ() * 16) + ((shortLoc >> 4) & 0xF);
        int x = (sectionPosition.getX() * 16) + ((shortLoc >> 8) & 0xF);
        return new Location(world, x, y, z);
    }


    public Map<Location, String> getBlockChanges() {
        return blockChanges;
    }

    public Map<UUID, Location> getPlayerMove() {
        return playerMove;
    }

    public void clear() {
        blockChanges.clear();
        playerMove.clear();
    }
}
