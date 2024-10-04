package alejandro.db;

import alejandro.utils.Environment;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mongo {
    private static final Logger logger = LoggerFactory.getLogger(Mongo.class);
    private MongoCollection<Document> collection;

    public Mongo() {
        String uri = Environment.getInstance().getVariables().get("MONGO_IP");
        String db = Environment.getInstance().getVariables().get("MONGO_DB");
        String cll = Environment.getInstance().getVariables().get("MONGO_COLLECTION");
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            collection = database.getCollection(cll);
        } catch (Exception e) {
            logger.error("Error while connecting to mongo db", e);
        }
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }
}
