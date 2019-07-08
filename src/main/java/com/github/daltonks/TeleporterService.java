package com.github.daltonks;

import javafx.util.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeleporterService implements Listener {
    private static final HashMap<Material, Integer> TERRACOTTA_MATERIAL_TO_ID_MAP = new HashMap<>();

    static {
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.BLACK_TERRACOTTA, 0);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.BLUE_TERRACOTTA, 1);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.BROWN_TERRACOTTA, 2);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.CYAN_TERRACOTTA, 3);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.GRAY_TERRACOTTA, 4);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.GREEN_TERRACOTTA, 5);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.LIGHT_BLUE_TERRACOTTA, 6);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.LIGHT_GRAY_TERRACOTTA, 7);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.LIME_TERRACOTTA, 8);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.MAGENTA_TERRACOTTA, 9);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.ORANGE_TERRACOTTA, 10);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.PINK_TERRACOTTA, 11);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.PURPLE_TERRACOTTA, 12);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.RED_TERRACOTTA, 13);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.WHITE_TERRACOTTA, 14);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.YELLOW_TERRACOTTA, 15);

        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.BLACK_GLAZED_TERRACOTTA, 16);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.BLUE_GLAZED_TERRACOTTA, 17);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.BROWN_GLAZED_TERRACOTTA, 18);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.CYAN_GLAZED_TERRACOTTA, 19);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.GRAY_GLAZED_TERRACOTTA, 20);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.GREEN_GLAZED_TERRACOTTA, 21);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 22);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 23);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.LIME_GLAZED_TERRACOTTA, 24);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.MAGENTA_GLAZED_TERRACOTTA, 25);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.ORANGE_GLAZED_TERRACOTTA, 26);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.PINK_GLAZED_TERRACOTTA, 27);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.PURPLE_GLAZED_TERRACOTTA, 28);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.RED_GLAZED_TERRACOTTA, 29);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.WHITE_GLAZED_TERRACOTTA, 30);
        TERRACOTTA_MATERIAL_TO_ID_MAP.put(Material.YELLOW_GLAZED_TERRACOTTA, 31);
    }

    private final SQLiteDB db;
    private final Logger logger;

    public TeleporterService(SQLiteDB db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Pair<Location, Integer> teleporterInfo = getTeleporterLocation(event.getBlockPlaced());
        if(teleporterInfo != null) {
            Location location = teleporterInfo.getKey();
            int terracottaMaterialId = teleporterInfo.getValue();

            try (PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO Teleporter VALUES (?, ?, ?, ?, ?)")) {
                statement.setString(1, location.getWorld().getUID().toString());
                statement.setInt(2, location.getBlockX());
                statement.setInt(3, location.getBlockY());
                statement.setInt(4, location.getBlockZ());
                statement.setInt(5, terracottaMaterialId);

                statement.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Creating teleporter error", e);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Pair<Location, Integer> teleporterInfo = getTeleporterLocation(event.getBlock());
        if(teleporterInfo != null) {
            Location location = teleporterInfo.getKey();

            try (PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM Teleporter WHERE WorldUUID = ? AND X = ? AND Y = ? AND Z = ?")) {
                statement.setString(1, location.getWorld().getUID().toString());
                statement.setInt(2, location.getBlockX());
                statement.setInt(3, location.getBlockY());
                statement.setInt(4, location.getBlockZ());

                statement.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Deleting teleporter error", e);
            }
        }
    }

    private Pair<Location, Integer> getTeleporterLocation(Block block) {
        Material material = block.getType();

        if(material == Material.GOLD_BLOCK) {
            Block topBlock = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
            Integer terracottaMaterialId = TERRACOTTA_MATERIAL_TO_ID_MAP.get(topBlock.getType());
            if(terracottaMaterialId != null) {
                return new Pair<>(block.getLocation(), terracottaMaterialId);
            }
        }
        else {
            Integer terracottaMaterialId = TERRACOTTA_MATERIAL_TO_ID_MAP.get(block.getType());
            if(terracottaMaterialId != null) {
                Block bottomBlock = block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ());
                if(bottomBlock.getType() == Material.GOLD_BLOCK) {
                    return new Pair<>(bottomBlock.getLocation(), terracottaMaterialId);
                }
            }
        }

        return null;
    }
}