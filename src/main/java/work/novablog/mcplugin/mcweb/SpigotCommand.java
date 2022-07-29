package work.novablog.mcplugin.mcweb;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SpigotCommand implements CommandExecutor {
    private Region getSelection(Player player) throws IncompleteRegionException {
        BukkitPlayer actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);

        return localSession.getSelection();
    }

    private void schemCommand(@NotNull Player player) {
        Region region;
        try {
            region = getSelection(player);
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.RED + "You must select a region first.");
            return;
        }

        try {
            SchematicWriter.save(region, "test.schem");
        } catch (WorldEditException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Cannot execute the command from console!");
            return true;
        }

        if(args.length == 0) return false;

        if ("schem".equals(args[0])) {
            schemCommand((Player) sender);
            sender.sendMessage(ChatColor.GREEN + "Schematic successfully saved.");
            return true;
        }else {
            return false;
        }
    }
}
