package net.chazza.levellingtools.util;

public enum EnchantmentEnum {
    SHARPNESS("DAMAGE_ALL"),
    BANE_OF_ARTHROPODS("DAMAGE_ARTHROPODS"),
    SMITE("DAMAGE_UNDEAD"),
    EFFICIENCY("DIG_SPEED"),
    UNBREAKING("DURABILITY"),
    FIRE_ASPECT("FIRE_ASPECT"),
    KNOCKBACK("KNOCKBACK"),
    FORTUNE("LOOT_BONUS_BLOCKS"),
    LOOTING("LOOT_BONUS_MOBS"),
    RESPIRATION("OXYGEN")
    ;

    private final String bukkitName;
    EnchantmentEnum(String bukkitName) { this.bukkitName = bukkitName; }
    public String getBukkitName() { return bukkitName; }

}
