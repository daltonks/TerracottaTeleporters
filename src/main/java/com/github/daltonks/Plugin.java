package com.github.daltonks;

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

            teleporterService = new TeleporterService(sqliteDB, getLogger());
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
