package ch.bader.budget.boundary;

import ch.bader.budget.TestUtils;
import com.mongodb.client.MongoClient;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import java.io.IOException;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class OverviewIT extends AbstractIT {

    @Inject
    MongoClient mongoClient;

    @Test
    void shouldLoadDb() {
        //arrange
        assertDoesNotThrow(() -> populateDatabaseFull(mongoClient));
    }

    @Test
    void shouldGetOverview() throws IOException, URISyntaxException, JSONException {
        //arrange
        populateDatabaseFull(mongoClient);

        String mills2022May1 = "1651363200000";
        JSONArray expectedJson = new JSONArray(JsonPath
            .from(TestUtils.loadFileAsString("json/overview.json"))
            .getList(""));

        //act + assert
        JSONArray response =
            new JSONArray(given().contentType(ContentType.JSON)
                                 .when()
                                 .param("dateLong", mills2022May1)
                                 .get("/budget/overview/list/")
                                 .then()
                                 .statusCode(HttpStatus.SC_OK)
                                 .extract().jsonPath().getList(""));

        JSONCompareResult compareResult = JSONCompare.compareJSON(expectedJson,
            response,
            JSONCompareMode.LENIENT);
        compareResult.getFieldFailures()
                     .forEach(fieldComparisonFailure -> System.out.println("Failed " +
                         fieldComparisonFailure.getField() + " Expected: " +
                         fieldComparisonFailure.getExpected() + " Actual: " +
                         fieldComparisonFailure.getActual()));
        assertTrue(compareResult.passed());
    }
}
