package gg.plugins.levellingtools.tool;

import org.bukkit.Material;

public class BlockXP
{
    private Material block;
    private byte data;
    private double experience;
    
    public BlockXP(final Material block, final Integer data, final double experience) {
        this.block = block;
        this.data = data.byteValue();
        this.experience = experience;
    }
    
    public Material getBlock() {
        return this.block;
    }
    
    public byte getData() {
        return this.data;
    }
    
    public double getExperience() {
        return this.experience;
    }
}
