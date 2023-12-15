package ch.bader.budget.boundary;

import ch.bader.budget.TestUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.restassured.specification.RequestSpecification;
import org.bson.BsonArray;
import org.bson.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static io.restassured.RestAssured.given;

abstract class AbstractIT {

    protected RequestSpecification givenWithAuth() {
        return given().auth().basic("budget-user", "password");
    }

    protected void populateDatabaseFull(final MongoClient mongoClient) throws IOException, URISyntaxException {
        populateMongoDbs(mongoClient);
    }

    private void populateMongoDbs(final MongoClient mongoClient) throws IOException, URISyntaxException {
        populateMongoDb(mongoClient, "virtualAccount");
        populateMongoDb(mongoClient, "realAccount");
        populateMongoDb(mongoClient, "transaction");
        populateMongoDb(mongoClient, "closingProcess");
        populateMongoDb(mongoClient, "scannedTransaction");
    }

    private void populateMongoDb(final MongoClient mongoClient,
                                 final String collectionName) throws IOException, URISyntaxException {
        final String collectionString = TestUtils.loadFileAsString("dump/budget/2023-02-04_" + collectionName + ".txt");

        final MongoDatabase db = mongoClient.getDatabase("budget");
        final MongoCollection<Document> collection = db.getCollection(collectionName);
        collection.deleteMany(new Document());

        final BsonArray bsonArray = BsonArray.parse(collectionString);

        final List<Document> jsonList = bsonArray.stream().map(b -> Document.parse(b.toString())).toList();

        collection.insertMany(jsonList);

    }

}
