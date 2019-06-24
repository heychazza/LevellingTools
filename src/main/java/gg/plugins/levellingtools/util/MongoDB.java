package gg.plugins.levellingtools.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.entity.BaseEntity;

public class MongoDB {

    private MongoClient client;
    private Datastore datastore;
    private LevellingTools plugin;

    public MongoDB(String prefix, String host, int port, String database, String username, String password, LevellingTools plugin) {
        this.plugin = plugin;

        String newPrefix = prefix.isEmpty() ? "" : "+" + prefix;
        String auth = (username.isEmpty() && password.isEmpty()) ? "" : username + ":" + password + "@";

        String connection = "mongodb" + newPrefix + "://" + auth + host + ":" + port + "/" + database + "?ssl=true&replicaSet=Cluster0-shard-0&authSource=" + username + "&retryWrites=true&w=majority";
        plugin.getLogger().info("Connection: " + connection);
        client = new MongoClient(new MongoClientURI(connection));

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