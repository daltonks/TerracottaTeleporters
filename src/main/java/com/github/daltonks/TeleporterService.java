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
                    // is found that isn't deleted in the world.
                    // Also delete them from the repo.

                    // TODO: Teleport player
                    itemStack.setAmount(itemStack.getAmount() - 1);
                }
            }
        }
    }

    private Teleporter addOrUpdateTeleporterAt(Block block) throws SQLException {
        Material material = block.getType();

        Integer terracottaMaterialID;
        Block goldBlock;

        if(material == Material.GOLD_BLOCK) {
            goldBlock = block;

            Block terracottaBlock = block.getRelative(0, 1, 0);
            terracottaMaterialID = TerracottaMaterialIDs.getID(terracottaBlock.getType());
            if(terracottaMaterialID == null) {
                return null;
            }
        } else {
            terracottaMaterialID = TerracottaMaterialIDs.getID(material);
            if(terracottaMaterialID == null) {
                return null;
            }

            goldBlock = block.getRelative(0, -1, 0);
            if(goldBlock.getType() != Material.GOLD_BLOCK) {
                return null;
            }
        }

        Block airBlock1 = goldBlock.getRelative(0, 2, 0);
        if(airBlock1.getType() != Material.AIR
                || airBlock1.getType() != Material.CAVE_AIR
                || airBlock1.getType() != Material.VOID_AIR) {
            return null;
        }

        Block airBlock2 = airBlock1.getRelative(0, 1, 0);
        if(airBlock2.getType() != Material.AIR
                || airBlock2.getType() != Material.CAVE_AIR
                || airBlock2.getType() != Material.VOID_AIR) {
            return null;
        }

        Teleporter teleporter = new Teleporter(
            block.getWorld().getName(),
            goldBlock.getX(),
            goldBlock.getY(),
            goldBlock.getZ(),
            terracottaMaterialID
        );

        repo.addOrUpdate(teleporter);

        return null;
    }
}
