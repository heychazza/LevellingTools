package net.chazza.levellingtools.tool;

import org.bukkit.Material;

public class BlockXP {

    private Material block;
    private byte data;
    private Integer experience;

    public BlockXP(Material block, Integer data, Integer experience) {
        this.block = block;
        this.data = data.byteValue();
        this.experience = experience;
    }

    public Material getBlock() {
        return block;
    }

    public byte getData() {
        return data;
    }

    public Integer getExperience() {
        return experience;
    }
}
