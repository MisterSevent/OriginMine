package net.mistersevent.originmine.chance;

import net.mistersevent.originmine.OriginMine;

public enum Chance {
    IRON(OriginMine.getInstance().getConfig().getInt("Blocks_Regeneration.Percentage.IRON")),
    GOLD(OriginMine.getInstance().getConfig().getInt("Blocks_Regeneration.Percentage.GOLD")),
    COAL(OriginMine.getInstance().getConfig().getInt("Blocks_Regeneration.Percentage.COAL")),
    LAPIS(OriginMine.getInstance().getConfig().getInt("Blocks_Regeneration.Percentage.LAPIS")),
    DIAMOND(OriginMine.getInstance().getConfig().getInt("Blocks_Regeneration.Percentage.DIAMOND")),
    REDSTONE(OriginMine.getInstance().getConfig().getInt("Blocks_Regeneration.Percentage.REDSTONE")),
    EMERALD(OriginMine.getInstance().getConfig().getInt("Blocks_Regeneration.Percentage.EMERALD"));

    private final double percentage;

    private Chance(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }
}
