package com.github.daltonks;

import java.io.File;
import java.nio.file.Paths;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteDB {
    private Connection connection;
    private final File directory;

    private final Logger logger;

    public SQLiteDB(File directory, Logger logger) {
        this.directory = directory;
        this.logger = logger;
    }

    public void openConnection() {
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }

            connection = DriverManager.getConnection(
                "jdbc:sqlite:" + Paths.get(directory.getPath(), "TerracottaTeleporters.db").toString()
            );

            migrate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SQLite DB initialization error", e);
        }
    }

    private void migrate() throws SQLException {
        try(Statement statement = connection.createStatement()) {

            statement.addBatch("CREATE TABLE IF NOT EXISTS \"Teleporter\" (\n" +
                               "\t\"WorldUUID\"\tTEXT NOT NULL,\n" +
                               "\t\"X\"\tINTEGER NOT NULL,\n" +
                               "\t\"Y\"\tINTEGER NOT NULL,\n" +
                               "\t\"Z\"\tINTEGER NOT NULL,\n" +
                               "\t\"Material\"\tINTEGER NOT NULL,\n" +
                               "\tPRIMARY KEY(\"X\",\"Y\",\"Z\", \"WorldUUID\")\n" +
                               ");");

            statement.addBatch("CREATE INDEX IF NOT EXISTS \"Teleporter.Material\" ON \"Teleporter\" (\n" +
                               "\t\"Material\"\n" +
                               ");");

            statement.executeBatch();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SQLite DB close error", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
