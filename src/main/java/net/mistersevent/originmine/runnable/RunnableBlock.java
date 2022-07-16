package net.mistersevent.originmine.runnable;

import net.mistersevent.originmine.chance.Chance;
import net.mistersevent.originmine.listener.server.MineListeners;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RunnableBlock extends BukkitRunnable {

    private final Random random = new Random();

    @Override
    public void run() {

        if (!MineListeners.blocksInCooldown.isEmpty()) {

            Iterator<Map.Entry<Block, Location>> iter =MineListeners.blocksInCooldown.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Block, Location> blocks = iter.next();

                Block block = blocks.getKey();
                setBlock(block);
                iter.remove();
            }
        }
    }

    private ItemStack setBlock(Block block) {

        List<Pair<Material, Double>> list = new ArrayList<>();
        list.add(new Pair<>(Material.EMERALD_ORE, Chance.EMERALD.getPercentage()));
        list.add(new Pair<>(Material.DIAMOND_ORE, Chance.DIAMOND.getPercentage()));
        list.add(new Pair<>(Material.COAL_ORE, Chance.COAL.getPercentage()));
        list.add(new Pair<>(Material.IRON_ORE, Chance.IRON.getPercentage()));
        list.add(new Pair<>(Material.REDSTONE_ORE, Chance.REDSTONE.getPercentage()));
        list.add(new Pair<>(Material.GOLD_ORE, Chance.GOLD.getPercentage()));
        list.add(new Pair<>(Material.LAPIS_ORE, Chance.LAPIS.getPercentage()));

        EnumeratedDistribution e = new EnumeratedDistribution(list);
        block.setType((Material) e.sample());
        return null;
    }

    protected boolean percentChance(double percent) {
        if (percent >= 0.0D && percent <= 100.0D) {
            double result = this.random.nextDouble() * 100.0D;
            return result <= percent;
        } else {
            throw new IllegalArgumentException("The percentage cannot be greater than 100 or less than 0");
        }
    }
}
