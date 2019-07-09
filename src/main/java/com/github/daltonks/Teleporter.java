package com.github.daltonks;

public class Teleporter {
    private final String worldName;
    private final int x;
    private final int y;
    private final int z;
    private final int terracottaMaterialID;

    public Teleporter(String worldName, int x, int y, int z, int terracottaMaterialID) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.terracottaMaterialID = terracottaMaterialID;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getTerracottaMaterialID() {
        return terracottaMaterialID;
    }
}