package com.fherdelpino.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDbConnectionFactory {

    private static final String username = "fherdelpino";
    private static final String password = "";
    private static final String host = "fherdelpino-mongodb-clu.mrclm.mongodb.net";
    private static final String connectionStringFormat = "mongodb+srv://%s:%s@%s/test?retryWrites=true&w=majority";

    public static MongoClient getConnection() {
        ConnectionString connectionString = new ConnectionString(String.format(connectionStringFormat, username, password, host));
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        return MongoClients.create(clientSettings);
    }
}
