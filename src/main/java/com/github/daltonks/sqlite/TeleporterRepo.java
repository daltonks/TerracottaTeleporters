package com.github.daltonks.sqlite;

import com.github.daltonks.Teleporter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public void add(Teleporter teleporter) {
        String sql = "INSERT INTO Teleporter VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());
            statement.setInt(5, teleporter.getTerracottaMaterialId());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Adding teleporter error", e);
        }
    }

    public void delete(Teleporter teleporter) {
        String sql = "DELETE FROM Teleporter WHERE WorldName = ? AND X = ? AND Y = ? AND Z = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Deleting teleporter error", e);
        }
    }

    public Teleporter getNext(Teleporter teleporter) {
        throw new NotImplementedException();
    }
}
