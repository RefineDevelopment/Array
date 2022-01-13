package xyz.refinedev.practice.managers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/12/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class MongoManager {

    private final Array plugin;
    private final BasicConfigurationFile config;

    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> profiles, clans, killEffects;

    public void init() {
        this.disableLogging();

        if (config.getBoolean("MONGO.URI-MODE")) {
            this.client = MongoClients.create(config.getString("MONGO.URI.CONNECTION_STRING"));
            this.database = client.getDatabase(config.getString("MONGO.URI.DATABASE"));

            plugin.logger("&7Initialized MongoDB successfully!");
            return;
        }

        boolean auth = config.getBoolean("MONGO.NORMAL.AUTHENTICATION.ENABLED");
        String host = config.getString("MONGO.NORMAL.HOST");
        int port = config.getInteger("MONGO.NORMAL.PORT");

        String uri = "mongodb://" + host + ":" + port;

        if (auth) {
            String username = config.getString("MONGO.NORMAL.AUTHENTICATION.USERNAME");
            String password = config.getString("MONGO.NORMAL.AUTHENTICATION.PASSWORD");
            uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
        }


        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(config.getString("MONGO.URI.DATABASE"));

        plugin.logger("&7Initialized MongoDB successfully!");
    }

    public void loadCollections() {
        profiles = this.database.getCollection("array-profiles");
        clans = this.database.getCollection("array-clans");
        killEffects = this.database.getCollection("array-killEffects");
    }

    public void shutdown() {
        plugin.consoleLog("&7Disconnecting &cMongo&7...");
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (this.client != null) this.client.close();
        plugin.consoleLog("&7Disconnected &cMongo &7Successfully!");
    }

    public void disableLogging() {
        Logger mongoLogger = Logger.getLogger( "com.mongodb" );
        mongoLogger.setLevel(Level.SEVERE);

        Logger legacyLogger = Logger.getLogger( "org.mongodb" );
        legacyLogger.setLevel(Level.SEVERE);
    }
}
