package com.codeitall.tools.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface PlayerData {

    Map<UUID, PlayerData> users = new HashMap<>();

    static Map<UUID, PlayerData> get() {
        return users;
    }

    static PlayerData get(UUID uuid) {
        return users.get(uuid);
    }

    String getUuid();

    String getUsername();

    void setUsername(String username);

    double getXp();

    void setXp(double xp);

    int getBlocksBroken();

    void setBlocksBroken(int blocks);

    int getLevel();

    void setLevel(int level);
}
