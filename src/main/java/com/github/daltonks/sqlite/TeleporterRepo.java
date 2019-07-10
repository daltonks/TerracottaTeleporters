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

    public boolean addOrUpdate(Teleporter teleporter) throws SQLException {
        Integer existingMaterial = null;
        String sql = "SELECT Material FROM Teleporter WHERE WorldName = ? AND X = ? AND Y = ? AND Z = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    existingMaterial = resultSet.getInt(1);
                }
            }
        }

        if(existingMaterial == null) {
            insert(teleporter);
            return true;
        } else if(existingMaterial != teleporter.getMaterialID()) {
            delete(teleporter.getWorldName(), teleporter.getX(), teleporter.getY(), teleporter.getZ());
            insert(teleporter);
            return true;
        }

        return false;
    }

    private void insert(Teleporter teleporter) throws SQLException {
        String sql = "INSERT INTO Teleporter VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());
            statement.setInt(5, teleporter.getMaterialID());

            statement.executeUpdate();
        }
    }

    public Teleporter getNext(Teleporter teleporter) throws SQLException {
        // Get rowID
        int rowID;

        String sql = "SELECT rowid FROM Teleporter WHERE WorldName = ? AND X = ? AND Y = ? AND Z = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, teleporter.getWorldName());
            statement.setInt(2, teleporter.getX());
            statement.setInt(3, teleporter.getY());
            statement.setInt(4, teleporter.getZ());

            try(ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                rowID = resultSet.getInt(1);
            }
        }

        // Try to get next teleporter with a greater row ID
        Teleporter nextTeleporter = getNextTeleporter(teleporter.getMaterialID(), rowID, true);

        if(nextTeleporter != null) {
            return nextTeleporter;
        }

        // Try to get next teleporter with a lesser row ID
        return getNextTeleporter(teleporter.getMaterialID(), rowID, false);
    }

    private Teleporter getNextTeleporter(int terracottaMaterialId, int rowID, boolean greaterThan) throws SQLException {
        // Get next rowID
        String sql = greaterThan
            ? "SELECT MIN(rowid) FROM Teleporter WHERE Material = ? AND rowid > ?"
            : "SELECT MIN(rowid) FROM Teleporter WHERE Material = ? AND rowid < ?";

        try(PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, terracottaMaterialId);
            statement.setInt(2, rowID);

            try(ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                rowID = resultSet.getInt(1);
                if(rowID == 0) {
                    return null;
                }
            }
        }

        // Get Teleporter from rowID
        sql = "SELECT WorldName, X, Y, Z, Material FROM Teleporter WHERE rowid = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, rowID);

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

    public void delete(String worldName, int x, int y, int z) throws SQLException {
        String sql = "DELETE FROM Teleporter WHERE WorldName = ? AND X = ? AND Y = ? AND Z = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, worldName);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);

            statement.executeUpdate();
        }
    }
}
