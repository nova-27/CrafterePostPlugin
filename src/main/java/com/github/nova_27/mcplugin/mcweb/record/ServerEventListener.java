package com.github.nova_27.mcplugin.mcweb.record;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.World;
import com.github.nova_27.mcplugin.mcweb.MCWeb;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServerEventListener {
    private final Map<Location, Integer> blockChanges;

    public ServerEventListener() {
        blockChanges = new HashMap<>();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                MCWeb.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Server.BLOCK_CHANGE,
                PacketType.Play.Server.MULTI_BLOCK_CHANGE
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                if(packet.getType() == PacketType.Play.Server.BLOCK_CHANGE) {
                    var loc = packet.getBlockPositionModifier().read(0).toLocation(event.getPlayer().getWorld());
                    var blockData = packet.getBlockData().read(0);
                    blockChanges.put(loc, getBlockStateId(blockData));
                }else if(packet.getType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
                    var sectionPos = packet.getSectionPositions().read(0);
                    var shortLocations = packet.getShortArrays().read(0);
                    var blockData = packet.getBlockDataArrays().read(0);
                    assert shortLocations.length == blockData.length;

                    for (int i = 0; i < shortLocations.length; i++) {
                        var loc = convertShortLocation(event.getPlayer().getWorld(), sectionPos, shortLocations[i]);
                        var stateId = getBlockStateId(blockData[i]);
                        blockChanges.put(loc, stateId);
                    }
                }
            }
        });
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

    private static Location convertShortLocation(World world, BlockPosition sectionPosition, short shortLoc) {
        int y = (sectionPosition.getY() * 16) + (shortLoc & 0xF);
        int z = (sectionPosition.getZ() * 16) + ((shortLoc >> 4) & 0xF);
        int x = (sectionPosition.getX() * 16) + ((shortLoc >> 8) & 0xF);
        return new Location(world, x, y, z);
    }
}
