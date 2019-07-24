package com.github.daltonks;

import com.github.daltonks.sqlite.TeleporterRepo;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class TeleporterService implements Listener {
    private final TeleporterRepo repo;

    public TeleporterService(TeleporterRepo repo) {
        this.repo = repo;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws SQLException {
        Teleporter teleporter = getTeleporterAt(event.getBlock());
        if(teleporter != null) {
            repo.delete(teleporter.getWorldName(), teleporter.getX(), teleporter.getY(), teleporter.getZ());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws SQLException {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Block clickedBlock = event.getClickedBlock();
        if(itemStack != null && itemStack.getType() == Material.CLAY_BALL && clickedBlock != null) {
            Teleporter teleporter = getTeleporterAt(clickedBlock);
            if(teleporter != null) {
                if(repo.addOrUpdate(teleporter)) {
                    player.sendMessage(ChatColor.GREEN + "Teleporter activated!");
                    player.getWorld().playEffect(
                        event.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5),
                        Effect.DRAGON_BREATH,
                        0
                    );
                    itemStack.setAmount(itemStack.getAmount() - 1);
                } else {
                    while(true) {
                        Teleporter nextTeleporterInRepo = repo.getNext(teleporter);
                        if(nextTeleporterInRepo == null) {
                            break;
                        }

                        World nextTeleporterWorld = Bukkit.createWorld(new WorldCreator(nextTeleporterInRepo.getWorldName()));
                        Block nextTeleporterBlock = nextTeleporterWorld.getBlockAt(
                            nextTeleporterInRepo.getX(),
                            nextTeleporterInRepo.getY(),
                            nextTeleporterInRepo.getZ()
                        );
                        Teleporter nextTeleporterInWorld = getTeleporterAt(nextTeleporterBlock);

                        if(nextTeleporterInWorld == null) {
                            repo.delete(
                                nextTeleporterInRepo.getWorldName(),
                                nextTeleporterInRepo.getX(),
                                nextTeleporterInRepo.getY(),
                                nextTeleporterInRepo.getZ()
                            );
                        } else {
                            Location teleportLocation =
                                nextTeleporterBlock.getRelative(0, 2, 0).getLocation().clone().add(0.5, 0, 0.5)
                                    .setDirection(player.getLocation().getDirection());

                            // Get vehicle before teleporting player,
                            // because it turns null upon teleporting player
                            Entity vehicle = player.getVehicle();

                            player.teleport(teleportLocation);

                            if (vehicle != null) {
                                vehicle.teleport(teleportLocation);
                                vehicle.addPassenger(player);
                            }

                            itemStack.setAmount(itemStack.getAmount() - 1);

                            player.getWorld().playEffect(teleportLocation, Effect.PORTAL_TRAVEL, 0);

                            break;
                        }
                    }
                }
            }
        }
    }

    private Teleporter getTeleporterAt(Block block) {
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

        return new Teleporter(
            block.getWorld().getName(),
            goldBlock.getX(),
            goldBlock.getY(),
            goldBlock.getZ(),
            terracottaMaterialID
        );
    }
}
