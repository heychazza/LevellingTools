package com.codeitforyou.tools.storage.sqlite;

import com.codeitforyou.tools.storage.PlayerData;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "players")
public class SQLitePlayerData implements PlayerData {

    @DatabaseField(id = true, useGetSet = true)
    private String uuid;

    @DatabaseField(useGetSet = true)
    private String username;

    @DatabaseField(defaultValue = "0", useGetSet = true, columnName = "xp")
    private double xp;

    @DatabaseField(defaultValue = "0", useGetSet = true, columnName = "blocks")
    private int blocksBroken;

    @DatabaseField(defaultValue = "1", useGetSet = true, columnName = "level")
    private int level;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
