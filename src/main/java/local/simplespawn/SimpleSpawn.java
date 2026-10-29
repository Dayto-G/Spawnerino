package local.simplespawn;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleSpawn extends JavaPlugin implements Listener {
    private File file;
    private YamlConfiguration data;

    @Override
    public void onEnable() {
        file = new File(getDataFolder(), "spawns.yml");
        data = new YamlConfiguration();
        try {
            if (file.exists()) data.load(file);
        } catch (Exception e) {
            getLogger().severe("Cannot read spawns.yml; disabling to protect saved spawns: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Objects.requireNonNull(getCommand("setspawn")).setExecutor(this);
        Objects.requireNonNull(getCommand("deletespawn")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getWorlds().forEach(this::applySpawn);
    }

    private String key(World world) {
        return "worlds." + world.getUID();
    }

    private void writeLocation(String path, Location location) {
        data.set(path + ".x", location.getBlockX());
        data.set(path + ".y", location.getBlockY());
        data.set(path + ".z", location.getBlockZ());
        data.set(path + ".yaw", location.getYaw());
        data.set(path + ".pitch", location.getPitch());
    }

    private Location readLocation(String path, World world) {
        return new Location(world, data.getInt(path + ".x"), data.getInt(path + ".y"),
                data.getInt(path + ".z"), (float) data.getDouble(path + ".yaw"),
                (float) data.getDouble(path + ".pitch", 0));
    }

    private void applySpawn(World world) {
        String path = key(world) + ".spawn";
        if (data.isConfigurationSection(path)) world.setSpawnLocation(readLocation(path, world));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        applySpawn(event.getWorld());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simplespawn.setspawn")) {
            sender.sendMessage("You do not have permission to manage spawn.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Run this command in game.");
            return true;
        }
        World world = player.getWorld();
        String path = key(world);
        boolean deleting = command.getName().equalsIgnoreCase("deletespawn");
        if (deleting && args.length != 0) return false;
        if (deleting && !data.isConfigurationSection(path + ".original")) {
            player.sendMessage("This world has no spawn set by SimpleSpawn.");
            return true;
        }
        Location target;
        if (deleting) {
            target = readLocation(path + ".original", world);
        } else {
            Location current = player.getLocation();
            try {
                SpawnPosition position = SpawnPosition.parse(args, current.getX(), current.getY(), current.getZ(),
                        current.getYaw(), current.getPitch(), world.getMinHeight(), world.getMaxHeight());
                target = new Location(world, position.x(), position.y(), position.z(), position.yaw(), position.pitch());
            } catch (IllegalArgumentException e) {
                player.sendMessage(e.getMessage());
                return true;
            }
        }
        Location before = world.getSpawnLocation();
        if (!world.setSpawnLocation(target)) {
            player.sendMessage("Could not change this world's spawn.");
            return true;
        }
        String snapshot = data.saveToString();
        if (deleting) {
            data.set(path, null);
        } else {
            if (!data.isConfigurationSection(path + ".original")) writeLocation(path + ".original", before);
            writeLocation(path + ".spawn", target);
        }
        try {
            data.save(file);
        } catch (IOException e) {
            world.setSpawnLocation(before);
            try { data.loadFromString(snapshot); } catch (Exception impossible) { getLogger().severe(impossible.toString()); }
            player.sendMessage("Could not save spawn; the change was undone. Check the server log.");
            getLogger().severe("Could not save spawns.yml: " + e.getMessage());
            return true;
        }
        // Persist the vanilla spawn as well, including when deleting our override.
        world.save();
        player.sendMessage(deleting ? "Previous world spawn restored." : String.format(Locale.ROOT,
                "World spawn saved at %d %d %d (yaw %.1f, pitch %.1f).",
                target.getBlockX(), target.getBlockY(), target.getBlockZ(), target.getYaw(), target.getPitch()));
        return true;
    }
}
