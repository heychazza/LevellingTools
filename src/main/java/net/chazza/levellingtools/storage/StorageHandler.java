package net.chazza.levellingtools.storage;

import java.util.UUID;

public interface StorageHandler {

    void pullData(String name, UUID uuid);

    void pushData(UUID uuid);

    default PlayerData getPlayer(UUID uuid) {
        return PlayerData.get().get(uuid);
    }
}
