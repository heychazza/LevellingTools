package gg.plugins.levellingtools.util;

import com.mongodb.*;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.entity.BaseEntity;

import java.util.Collections;

public class MongoDB {

    private MongoClient client;
    private Datastore datastore;
    private LevellingTools plugin;

    public MongoDB(String host, int port, String database, String username, String password, LevellingTools plugin) {
        this.plugin = plugin;

        client = new MongoClient(host, port);

        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .socketTimeout(60000) // Wait 1m for a query to finish, https://jira.mongodb.org/browse/JAVA-1076
                .connectTimeout(15000) // Try the initial connection for 15s, http://blog.mongolab.com/2013/10/do-you-want-a-timeout/
                .maxConnectionIdleTime(10000) // Keep idle connections for 10m, so we discard failed connections quickly
                .readPreference(ReadPreference.primaryPreferred()) // Read from the primary, if not available use a secondary
                //.addServerListener(this)
                .build();


        if (username.isEmpty() && password.isEmpty()) {
            client = new MongoClient(new ServerAddress(host, port), options);

        } else {
            client = new MongoClient(new ServerAddress(host, port),
                    Collections.singletonList(credential), options);
        }

        client.setWriteConcern(WriteConcern.SAFE);

        datastore = new Morphia().mapPackage(BaseEntity.class.getPackage().getName()).createDatastore(client, database);
        datastore.ensureIndexes();
        datastore.ensureCaps();
    }

    public Datastore getDB() {
        return datastore;
    }

    public MongoClient getClient() {
        return client;
    }
}