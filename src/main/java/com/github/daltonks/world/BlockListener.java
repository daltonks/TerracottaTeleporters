package com.github.daltonks.world;

import com.github.daltonks.sqlite.TeleporterRepo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    private final TeleporterRepo repo;

    public BlockListener(TeleporterRepo repo) {
        this.repo = repo;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Teleporter teleporter = Teleporter.getAtBlock(event.getBlockPlaced());
        if(teleporter != null) {
            repo.addTeleporter(teleporter);
        }
    }
}
