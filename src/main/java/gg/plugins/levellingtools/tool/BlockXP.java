package gg.plugins.levellingtools.tool;

import org.bukkit.Material;

public class BlockXP
{
    private Material block;
    private byte data;
    private double xp;

    public BlockXP(final Material block, final Integer data, final double xp) {
        this.block = block;
        this.data = data.byteValue();
        this.xp = xp;
    }
    
    public Material getBlock() {
        return this.block;
    }
    
    public byte getData() {
        return this.data;
    }

    public double getXp() {
        return this.xp;
    }
}
