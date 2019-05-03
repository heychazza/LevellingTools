package net.chazza.levellingtools.util;

public class LevellingUtil {

    public LevellingUtil(){}

    private static double baseMulti = 0;

    public long getLevelFromExp(int xp) {
        return Math.round(baseMulti * Math.sqrt(xp)) + 1;
    }

    public void setBaseMulti(double baseMulti) {
        LevellingUtil.baseMulti = baseMulti;
    }

    public double getBaseMulti() {
        return baseMulti;
    }
}
