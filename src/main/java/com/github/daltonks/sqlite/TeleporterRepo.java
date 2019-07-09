package com.github.daltonks.sqlite;

import com.github.daltonks.Teleporter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeleporterRepo {
    private final SQLiteDB db;

    public TeleporterRepo(SQLiteDB db) {
        this.db = db;
    }

    public void addOrUpdate(Teleporter teleporter) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Teleporter VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());
            statement.setInt(5, teleporter.getTerracottaMaterialID());

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
        Teleporter nextTeleporter = getNextTeleporter(teleporter.getTerracottaMaterialID(), rowId, true);

        if(nextTeleporter != null) {
            return nextTeleporter;
        }

        // Try to get next teleporter with a lesser row ID
        return getNextTeleporter(teleporter.getTerracottaMaterialID(), rowId, false);
    }


    private Teleporter getNextTeleporter(int terracottaMaterialId, int rowId, boolean greaterThan) throws SQLException {
        // Get next rowid
        String sql = greaterThan
            ? "SELECT MIN(rowid) FROM Teleporter WHERE Material = ? AND rowid > ?"
            : "SELECT MIN(rowid) FROM Teleporter WHERE Material = ? AND rowid < ?";

        try(PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, terracottaMaterialId);
            statement.setInt(2, rowId);

            try(ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                rowId = resultSet.getInt("rowid");
            }
        }

        // Get Teleporter from rowid
        sql = "SELECT * FROM Teleporter WHERE rowid = ?";
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
