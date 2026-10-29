package local.simplespawn;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Locale;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
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
        boolean deleting = command.getName().equalsIgnoreCase("deletespawn");
        World world;
        Location current;
        String[] coordinates;
        boolean fromPlayer = sender instanceof Player;
        if (sender instanceof Player player) {
            world = player.getWorld();
            current = player.getLocation();
            coordinates = args;
            if (deleting && args.length != 0) return false;
        } else if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            if ((deleting && args.length != 1) || (!deleting && (args.length < 4 || args.length > 6))) {
                sender.sendMessage(deleting ? "Usage: deletespawn <world>"
                        : "Usage: setspawn <world> <x> <y> <z> [yaw [pitch]]");
                return true;
            }
            world = getServer().getWorld(args[0]);
            if (world == null) {
                sender.sendMessage("World is not loaded: " + args[0]);
                return true;
            }
            current = world.getSpawnLocation();
            coordinates = Arrays.copyOfRange(args, 1, args.length);
        } else {
            sender.sendMessage("Run this command as a player, from the server console, or through RCON.");
            return true;
        }
        String path = key(world);
        if (deleting && !data.isConfigurationSection(path + ".original")) {
            sender.sendMessage("This world has no spawn set by SimpleSpawn.");
            return true;
        }
        Location target;
        if (deleting) {
            target = readLocation(path + ".original", world);
        } else {
            try {
                SpawnPosition position = fromPlayer
                        ? SpawnPosition.parse(coordinates, current.getX(), current.getY(), current.getZ(),
                                current.getYaw(), current.getPitch(), world.getMinHeight(), world.getMaxHeight())
                        : SpawnPosition.parseConsole(coordinates, current.getYaw(), current.getPitch(),
                                world.getMinHeight(), world.getMaxHeight());
                target = new Location(world, position.x(), position.y(), position.z(), position.yaw(), position.pitch());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(e.getMessage());
                return true;
            }
        }
        Location before = world.getSpawnLocation();
        if (!world.setSpawnLocation(target)) {
            sender.sendMessage("Could not change this world's spawn.");
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
            sender.sendMessage("Could not save spawn; the change was undone. Check the server log.");
            getLogger().severe("Could not save spawns.yml: " + e.getMessage());
            return true;
        }
        // Persist the vanilla spawn as well, including when deleting our override.
        world.save();
        sender.sendMessage(deleting ? "Previous spawn restored for " + world.getName() + "." : String.format(Locale.ROOT,
                "Spawn saved for %s at %d %d %d (yaw %.1f, pitch %.1f).",
                world.getName(), target.getBlockX(), target.getBlockY(), target.getBlockZ(), target.getYaw(), target.getPitch()));
        return true;
    }
}
