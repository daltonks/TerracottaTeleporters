package com.github.daltonks.sqlite;

import com.github.daltonks.Teleporter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public void addOrUpdate(Teleporter teleporter) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Teleporter VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());
            statement.setInt(5, teleporter.getTerracottaMaterialId());

            statement.executeUpdate();
        }
    }

    public void delete(Teleporter teleporter) throws SQLException {
        String sql = "DELETE FROM Teleporter WHERE WorldName = ? AND X = ? AND Y = ? AND Z = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());

            statement.executeUpdate();
        }
    }

    public Teleporter getNext(Teleporter teleporter) throws SQLException {
        int rowId;

        // Get rowid
        String sql = "SELECT rowid FROM Teleporter WHERE WorldName = ? AND X = ? AND Y = ? AND Z = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());

            try(ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                rowId = resultSet.getInt("rowid");
            }
        }

        // Try to get next teleporter with a greater row ID
        sql = "SELECT MIN(rowid) FROM Teleporter WHERE Material = ? AND rowid > ?";
        try(PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, teleporter.getTerracottaMaterialId());
            statement.setInt(2, rowId);

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    int nextRowId = resultSet.getInt("rowid");
                    return get(nextRowId);
                }
            }
        }

        // Try to get next teleporter with a lesser row ID
        sql = "SELECT MIN(rowid) FROM Teleporter WHERE Material = ? AND rowid < ?";
        try(PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, teleporter.getTerracottaMaterialId());
            statement.setInt(2, rowId);

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    int nextRowId = resultSet.getInt("rowid");
                    return get(nextRowId);
                }
            }
        }

        return null;
    }

    private Teleporter get(int rowId) throws SQLException {
        String sql = "SELECT * FROM Teleporter WHERE rowid = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, rowId);

            try(ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();

                return new Teleporter(
                    resultSet.getString("WorldName"),
                    resultSet.getInt("X"),
                    resultSet.getInt("Y"),
                    resultSet.getInt("Z"),
                    resultSet.getInt("Material")
                );
            }
        }
    }
}
