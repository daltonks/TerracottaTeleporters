package com.github.daltonks;

public class Teleporter {
    private final String worldName;
    private final int x;
    private final int y;
    private final int z;
    private final int materialID;

    public Teleporter(String worldName, int x, int y, int z, int materialID) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.materialID = materialID;
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

    public int getMaterialID() {
        return materialID;
    }
}