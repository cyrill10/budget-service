package ch.bader.budget.boundary;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FillDataBaseTest extends AbstractIT {

    @Test
    @Disabled
    void shouldLoadDb() {
        //arrange
        final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        assertDoesNotThrow(() -> populateDatabaseFull(mongoClient));
    }
}
