package work.novablog.mcplugin.mcweb;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class MCWeb extends JavaPlugin {
    private static MCWeb instance;
    public static MCWeb getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if(!getDataFolder().isDirectory() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "Failed to create the plugin directory!");
            return;
        }

        if(!(Bukkit.getPluginManager().getPlugin("WorldEdit") instanceof WorldEditPlugin)) {
            getLogger().log(Level.SEVERE, "Could not find WorldEdit plugin!");
            return;
        }

        Objects.requireNonNull(getCommand("mcweb")).setExecutor(new SpigotCommand());
    }
}