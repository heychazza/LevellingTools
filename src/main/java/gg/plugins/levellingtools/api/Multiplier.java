package gg.plugins.levellingtools.api;

public class Multiplier {
    private String id;
    private double multiplier;

    public Multiplier(String id, double multiplier) {
        this.id = id.toLowerCase();
        this.multiplier = multiplier;
    }

    public String getId() {
        return id;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getPermission() {
        return "ltools.multi." + id;
    }
}
