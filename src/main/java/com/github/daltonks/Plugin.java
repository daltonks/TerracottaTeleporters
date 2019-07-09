package com.github.daltonks;

import com.github.daltonks.sqlite.SQLiteDB;
import com.github.daltonks.sqlite.TeleporterRepo;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private boolean initialized;

    private SQLiteDB sqliteDB;
    private TeleporterService teleporterService;

    @Override
    public void onEnable() {
        if (!initialized) {
            initialized = true;

            sqliteDB = new SQLiteDB(getDataFolder(), getLogger());
            sqliteDB.openConnection();

            TeleporterRepo teleporterRepo = new TeleporterRepo(sqliteDB);

            teleporterService = new TeleporterService(teleporterRepo);
        } else {
            sqliteDB.openConnection();
        }

        getServer().getPluginManager().registerEvents(teleporterService, this);
    }

    @Override
    public void onDisable() {
        sqliteDB.closeConnection();
    }
}
