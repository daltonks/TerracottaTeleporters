package com.github.daltonks;

import com.github.daltonks.sqlite.SQLiteDB;
import com.github.daltonks.sqlite.TeleporterRepo;
import com.github.daltonks.world.BlockListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private boolean initialized;

    private BlockListener blockListener;
    private SQLiteDB sqliteDB;

    @Override
    public void onEnable() {
        if (!initialized) {
            initialized = true;

            sqliteDB = new SQLiteDB(getDataFolder(), getLogger());
            sqliteDB.openConnection();

            TeleporterRepo teleporterRepo = new TeleporterRepo(sqliteDB, getLogger());

            blockListener = new BlockListener(teleporterRepo);
        } else {
            sqliteDB.openConnection();
        }

        getServer().getPluginManager().registerEvents(blockListener, this);
    }

    @Override
    public void onDisable() {
        sqliteDB.closeConnection();
    }
}
