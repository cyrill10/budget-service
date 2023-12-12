package ch.bader.budget.boundary;

import ch.bader.budget.TestUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.bson.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractIT {

    protected void populateDatabaseFull(MongoClient mongoClient) throws IOException, URISyntaxException {
        populateMongoDbs(mongoClient);
    }

    private void populateMongoDbs(MongoClient mongoClient) throws IOException, URISyntaxException {
        populateMongoDb(mongoClient, "virtualAccount");
        populateMongoDb(mongoClient, "realAccount");
        populateMongoDb(mongoClient, "transaction");
        populateMongoDb(mongoClient, "closingProcess");
        populateMongoDb(mongoClient, "scannedTransaction");
    }

    private void populateMongoDb(MongoClient mongoClient,
                                 String collectionName) throws IOException, URISyntaxException {
        String virtualAccountsString = TestUtils.loadFileAsString(
            "dump/budget/2023-02-04_" + collectionName + ".txt");

        MongoDatabase db = mongoClient.getDatabase("budget");
        MongoCollection<Document> collection = db.getCollection(collectionName);
        collection.deleteMany(new Document());
        List<Document> jsonList = new ArrayList<>();
        JSONArray array = JSONArray.fromObject(virtualAccountsString);
        for (
            Object object : array) {
            JSONObject jsonStr = (JSONObject) JSONSerializer.toJSON(object);
            Document jsnObject = Document.parse(jsonStr.toString());
            jsonList.add(jsnObject);

        }
        collection.insertMany(jsonList);

    }

}
