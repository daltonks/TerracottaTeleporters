package com.github.daltonks;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private boolean initialized;

    private SQLiteDB sqliteDB;
    private PortalService portalService;

    @Override
    public void onEnable() {
        if (!initialized) {
            initialized = true;

            sqliteDB = new SQLiteDB(getDataFolder(), getLogger());
            sqliteDB.openConnection();

            portalService = new PortalService(sqliteDB);
        } else {
            sqliteDB.openConnection();
        }

        getServer().getPluginManager().registerEvents(new BlockListener(portalService), this);
    }

    @Override
    public void onDisable() {
        sqliteDB.closeConnection();
    }
}
