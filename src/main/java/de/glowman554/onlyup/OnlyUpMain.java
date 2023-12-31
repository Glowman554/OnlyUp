package de.glowman554.onlyup;

import de.glowman554.onlyup.db.Database;
import de.glowman554.onlyup.db.SQLiteDatabaseConnection;
import de.glowman554.onlyup.listener.PlayerQuitListener;
import de.glowman554.onlyup.tasks.TimerTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;

public final class OnlyUpMain extends JavaPlugin {

    private static OnlyUpMain instance;

    public OnlyUpMain() {
        instance = this;
    }

    private final FileConfiguration config = getConfig();

    private TimerTask timer;
    private Database database;

    private Coordinate hologram;
    private String world;


    @Override
    public void onLoad() {
        config.addDefault("world", "world");
        config.addDefault("startY", 80);
        config.addDefault("goalY", 100);
        config.addDefault("database.type", "sqlite");

        config.addDefault("hologram.x", 100);
        config.addDefault("hologram.y", 100);
        config.addDefault("hologram.z", 100);

        config.options().copyDefaults(true);

        saveConfig();
        reloadConfig();
    }

    private Coordinate loadCoordinate(String id) {
        return new Coordinate(config.getInt(id + ".x"), config.getInt(id + ".y"), config.getInt(id + ".z"));
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        String databaseType = config.getString("database.type");
        switch (databaseType) {
            case "sqlite":
                try {
                    database = new SQLiteDatabaseConnection(new File(getDataFolder(), "database.db"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new IllegalStateException("Unknown database type " + databaseType);
        }

        world = config.getString("world");

        hologram = loadCoordinate("hologram");
        getLogger().log(Level.INFO, hologram.toString());

        timer = new TimerTask(world, config.getInt("startY"), config.getInt("goalY"));
        timer.runTaskTimer(this, 1, 1);
    }

    @Override
    public void onDisable() {
    }

    public static OnlyUpMain getInstance() {
        return instance;
    }

    public TimerTask getTimer() {
        return timer;
    }

    public Database getDatabase() {
        return database;
    }

    public String getWorld() {
        return world;
    }

    public Coordinate getHologram() {
        return hologram;
    }
}
