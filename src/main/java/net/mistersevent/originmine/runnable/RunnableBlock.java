package net.mistersevent.originmine.runnable;

import jdk.jfr.internal.tool.Main;
import net.mistersevent.originmine.OriginMine;
import net.mistersevent.originmine.listeners.PlayerBreakListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RunnableBlock extends BukkitRunnable {

    @Override
    public void run() {


        if (!PlayerBreakListener.blocksInCooldown.isEmpty()) {

            for (Map.Entry<Block, Location> blocks : PlayerBreakListener.blocksInCooldown.entrySet()) {

                Block block = blocks.getKey();

                List<String> blocksRegenerate = OriginMine.getInstance().getConfig().getStringList("Blocks_Regeneration");

                int index = new Random().nextInt(blocksRegenerate.size());
                String regenerate = blocksRegenerate.get(index);

                block.setType(Material.getMaterial(regenerate.toUpperCase()));

                PlayerBreakListener.blocksInCooldown.remove(block);
            }
        }
    }
}
