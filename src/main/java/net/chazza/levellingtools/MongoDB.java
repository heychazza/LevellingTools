package net.chazza.levellingtools;

import com.mongodb.*;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import net.chazza.levellingtools.entity.BaseEntity;

public class MongoDB {

    private static Datastore datastore = null;

    public MongoDB(LevellingTools levellingTools) {
        MongoClientOptions mongoOptions = MongoClientOptions.builder()
                .socketTimeout(60000) // Wait 1m for a query to finish, https://jira.mongodb.org/browse/JAVA-1076
                .connectTimeout(15000) // Try the initial connection for 15s, http://blog.mongolab.com/2013/10/do-you-want-a-timeout/
                .maxConnectionIdleTime(600000) // Keep idle connections for 10m, so we discard failed connections quickly
                .readPreference(ReadPreference.primaryPreferred()) // Read from the primary, if not available use a secondary
                .build();
        MongoClient mongoClient;
        mongoClient = new MongoClient(new ServerAddress(levellingTools.getConfig().getString("settings.database.host"),
                levellingTools.getConfig().getInt("settings.database.port")), mongoOptions);

        mongoClient.setWriteConcern(WriteConcern.SAFE);
        datastore = new Morphia().mapPackage(BaseEntity.class.getPackage().getName())
                .createDatastore(mongoClient, levellingTools.getConfig().getString("settings.database.name"));
        datastore.ensureIndexes();
        datastore.ensureCaps();
    }

    public static Datastore getDatabase() {
        return datastore;
    }
}
