package alejandro.services;

import alejandro.db.Mongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoServices {
    private static final Logger logger = LoggerFactory.getLogger(MongoServices.class);
    Mongo mongo;

    public MongoServices() {
        mongo = new Mongo();
    }

    public String getFingerprint(String uid) {
        String path = "/" + uid + "/mpi";
        Document document = mongo.getCollection().find(Filters.eq("path", path)).first();

        if (document != null) {
            logger.info("fingerprint of {} correctly found {}", uid, document.getString("fingerprint"));
            return document.getString("fingerprint");
        } else {
            logger.error("couldnt find fingerprint of {}", uid);
            return "NO-FINGERPRINT";
        }
    }
}
