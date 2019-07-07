package com.github.daltonks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    private final PortalService portalService;

    public BlockListener(PortalService portalService) {
        this.portalService = portalService;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material material = event.getBlockPlaced().getType();
        if(portalService.isPortalMaterial(material)) {

        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material material = event.getBlock().getType();
        if(portalService.isPortalMaterial(material)) {

        }
    }
}
