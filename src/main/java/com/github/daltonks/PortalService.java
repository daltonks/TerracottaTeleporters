package com.github.daltonks;

import org.bukkit.Material;

import java.util.HashMap;

public class PortalService {
    private static HashMap<Material, Integer> materialToIdMap = new HashMap<>();

    static {
        materialToIdMap.put(Material.BLACK_TERRACOTTA, 0);
        materialToIdMap.put(Material.BLUE_TERRACOTTA, 1);
        materialToIdMap.put(Material.BROWN_TERRACOTTA, 2);
        materialToIdMap.put(Material.CYAN_TERRACOTTA, 3);
        materialToIdMap.put(Material.GRAY_TERRACOTTA, 4);
        materialToIdMap.put(Material.GREEN_TERRACOTTA, 5);
        materialToIdMap.put(Material.LIGHT_BLUE_TERRACOTTA, 6);
        materialToIdMap.put(Material.LIGHT_GRAY_TERRACOTTA, 7);
        materialToIdMap.put(Material.LIME_TERRACOTTA, 8);
        materialToIdMap.put(Material.MAGENTA_TERRACOTTA, 9);
        materialToIdMap.put(Material.ORANGE_TERRACOTTA, 10);
        materialToIdMap.put(Material.PINK_TERRACOTTA, 11);
        materialToIdMap.put(Material.PURPLE_TERRACOTTA, 12);
        materialToIdMap.put(Material.RED_TERRACOTTA, 13);
        materialToIdMap.put(Material.WHITE_TERRACOTTA, 14);
        materialToIdMap.put(Material.YELLOW_TERRACOTTA, 15);

        materialToIdMap.put(Material.BLACK_GLAZED_TERRACOTTA, 16);
        materialToIdMap.put(Material.BLUE_GLAZED_TERRACOTTA, 17);
        materialToIdMap.put(Material.BROWN_GLAZED_TERRACOTTA, 18);
        materialToIdMap.put(Material.CYAN_GLAZED_TERRACOTTA, 19);
        materialToIdMap.put(Material.GRAY_GLAZED_TERRACOTTA, 20);
        materialToIdMap.put(Material.GREEN_GLAZED_TERRACOTTA, 21);
        materialToIdMap.put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 22);
        materialToIdMap.put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 23);
        materialToIdMap.put(Material.LIME_GLAZED_TERRACOTTA, 24);
        materialToIdMap.put(Material.MAGENTA_GLAZED_TERRACOTTA, 25);
        materialToIdMap.put(Material.ORANGE_GLAZED_TERRACOTTA, 26);
        materialToIdMap.put(Material.PINK_GLAZED_TERRACOTTA, 27);
        materialToIdMap.put(Material.PURPLE_GLAZED_TERRACOTTA, 28);
        materialToIdMap.put(Material.RED_GLAZED_TERRACOTTA, 29);
        materialToIdMap.put(Material.WHITE_GLAZED_TERRACOTTA, 30);
        materialToIdMap.put(Material.YELLOW_GLAZED_TERRACOTTA, 31);
    }

    private final SQLiteDB db;

    public PortalService(SQLiteDB db) {
        this.db = db;
    }

    public boolean isPortalMaterial(Material material) {
        return materialToIdMap.get(material) != null;
    }
}
