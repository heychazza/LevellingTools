package net.chazza.levellingtools.tool;

import org.bukkit.Material;

public class LevellingXP {

    private Material block;
    private Integer data;
    private Integer experience;

    public LevellingXP(Material block, Integer data, Integer experience) {
        this.block = block;
        this.data = data;
        this.experience = experience;
    }

    public Material getBlock() {
        return block;
    }

    public Integer getData() {
        return data;
    }

    public Integer getExperience() {
        return experience;
    }
}
