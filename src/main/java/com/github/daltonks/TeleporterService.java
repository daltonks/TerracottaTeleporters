package com.github.daltonks;

import com.github.daltonks.sqlite.TeleporterRepo;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;

public class TeleporterService implements Listener {
    private final TeleporterRepo repo;

    public TeleporterService(TeleporterRepo repo) {
        this.repo = repo;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws SQLException {
        addOrUpdateTeleporterAt(event.getBlockPlaced());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws SQLException {
        ItemStack itemStack = event.getItem();
        Block clickedBlock = event.getClickedBlock();
        if(itemStack != null && itemStack.getType() == Material.CLAY_BALL && clickedBlock != null) {
            Teleporter teleporter = addOrUpdateTeleporterAt(clickedBlock);
            if(teleporter != null) {
                Teleporter nextTeleporter = repo.getNext(teleporter);
                if(nextTeleporter != null) {
                    // TODO: Keep trying the next teleporter until one
                    // is found that isn't deleted in the world

                    // TODO: Teleport player
                    itemStack.setAmount(itemStack.getAmount() - 1);
                }
            }
        }
    }

    private Teleporter addOrUpdateTeleporterAt(Block block) throws SQLException {
        Material material = block.getType();

        if(material == Material.GOLD_BLOCK) {
            Block topBlock = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
            Integer terracottaMaterialId = TerracottaMaterialIDs.getID(topBlock.getType());
            if(terracottaMaterialId != null) {
                Teleporter teleporter = new Teleporter(
                    block.getWorld().getName(),
                    block.getX(),
                    block.getY(),
                    block.getZ(),
                    terracottaMaterialId
                );

                repo.addOrUpdate(teleporter);
            }
        } else {
            Integer terracottaMaterialId = TerracottaMaterialIDs.getID(block.getType());
            if(terracottaMaterialId != null) {
                Block bottomBlock = block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ());
                if(bottomBlock.getType() == Material.GOLD_BLOCK) {
                    Teleporter teleporter = new Teleporter(
                        bottomBlock.getWorld().getName(),
                        bottomBlock.getX(),
                        bottomBlock.getY(),
                        bottomBlock.getZ(),
                        terracottaMaterialId
                    );

                    repo.addOrUpdate(teleporter);
                }
            }
        }

        return null;
    }
}
