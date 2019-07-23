package gg.plugins.levellingtools.storage;

import java.util.UUID;

public interface StorageHandler {

    void pullData(UUID uuid);

    void pushData(UUID uuid);

    PlayerData getPlayer(UUID uuid);
}
