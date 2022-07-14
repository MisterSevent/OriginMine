package net.mistersevent.originmine.listeners;

import net.mistersevent.originmine.OriginMine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;

public class PlayerBreakListener implements Listener {

    public static HashMap<Block, Location> blocksInCooldown = new HashMap<>();

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent e) {

        Block b = e.getBlock();
        BlockState state = b.getState();
        blocksInCooldown.put(b, b.getLocation());
        b.setType(Material.AIR);
    }
}
