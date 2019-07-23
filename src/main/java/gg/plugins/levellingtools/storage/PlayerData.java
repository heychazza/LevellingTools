package gg.plugins.levellingtools.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface PlayerData {

    Map<UUID, PlayerData> users = new HashMap<>();

    static Map<UUID, PlayerData> get() {
        return users;
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
