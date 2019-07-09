package com.github.daltonks.sqlite;

import com.github.daltonks.world.Teleporter;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeleporterRepo {
    private final SQLiteDB db;
    private final Logger logger;

    public TeleporterRepo(SQLiteDB db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public void addTeleporter(Teleporter teleporter) {
        int terracottaMaterialId = teleporter.getTerracottaMaterialId();

        try (PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO Teleporter VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, teleporter.getWorld().getUID().toString());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());
            statement.setInt(5, terracottaMaterialId);

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Creating teleporter error", e);
        }
    }

    public void deleteTeleporter(Teleporter teleporter) {
        try (PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM Teleporter WHERE WorldUUID = ? AND X = ? AND Y = ? AND Z = ?")) {
            statement.setString(1, teleporter.getWorld().getUID().toString());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Deleting teleporter error", e);
        }
    }
}
