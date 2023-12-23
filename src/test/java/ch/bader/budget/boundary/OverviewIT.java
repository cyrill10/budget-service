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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

        final long mills2022May1 = ZonedDateTime
            .of(LocalDate.of(2022, 5, 1), LocalTime.NOON, ZoneId.of("Z"))
            .toInstant()
            .toEpochMilli();
        
        final JSONArray expectedJson = new JSONArray(JsonPath
            .from(TestUtils.loadFileAsString("json/overview.json"))
            .getList(""));

        //act + assert
        final JSONArray response = new JSONArray(givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("dateLong", mills2022May1)
            .get("/budget/overview/list/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .jsonPath()
            .getList(""));

        final JSONCompareResult compareResult = JSONCompare.compareJSON(expectedJson,
            response,
            JSONCompareMode.LENIENT);

        compareResult
            .getFieldUnexpected()
            .forEach(filedUnexpected -> System.out.println("Failed " + filedUnexpected.getField() + " Expected: " + filedUnexpected.getExpected() + " Actual: " + filedUnexpected.getActual()));
        compareResult
            .getFieldMissing()
            .forEach(fieldMissingFailure -> System.out.println("Failed " + fieldMissingFailure.getField() + " Expected: " + fieldMissingFailure.getExpected() + " Actual: " + fieldMissingFailure.getActual()));
        compareResult
            .getFieldFailures()
            .forEach(fieldComparisonFailure -> System.out.println("Failed " + fieldComparisonFailure.getField() + " Expected: " + fieldComparisonFailure.getExpected() + " Actual: " + fieldComparisonFailure.getActual()));
        assertTrue(compareResult.passed());
    }

    @Test
    void shouldGetOverviewWithYearMonthBalanceCalculatedFully() throws IOException, URISyntaxException, JSONException {
        //arrange
        populateDatabaseFull(mongoClient);

        // calculate YearMonth Balance
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .queryParam("year", 2022)
            .queryParam("month", 6)
            .post("/budget/yearMonthBalance")
            .then()
            .statusCode(HttpStatus.SC_NO_CONTENT);

        final long mills2022May1 = ZonedDateTime
            .of(LocalDate.of(2022, 5, 1), LocalTime.NOON, ZoneId.of("Z"))
            .toInstant()
            .toEpochMilli();
        final JSONArray expectedJson = new JSONArray(JsonPath
            .from(TestUtils.loadFileAsString("json/overview.json"))
            .getList(""));

        //act + assert
        final JSONArray response = new JSONArray(givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("dateLong", mills2022May1)
            .get("/budget/overview/list/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .jsonPath()
            .getList(""));

        final JSONCompareResult compareResult = JSONCompare.compareJSON(expectedJson,
            response,
            JSONCompareMode.LENIENT);

        compareResult
            .getFieldUnexpected()
            .forEach(filedUnexpected -> System.out.println("Failed " + filedUnexpected.getField() + " Expected: " + filedUnexpected.getExpected() + " Actual: " + filedUnexpected.getActual()));
        compareResult
            .getFieldMissing()
            .forEach(fieldMissingFailure -> System.out.println("Failed " + fieldMissingFailure.getField() + " Expected: " + fieldMissingFailure.getExpected() + " Actual: " + fieldMissingFailure.getActual()));
        compareResult
            .getFieldFailures()
            .forEach(fieldComparisonFailure -> System.out.println("Failed " + fieldComparisonFailure.getField() + " Expected: " + fieldComparisonFailure.getExpected() + " Actual: " + fieldComparisonFailure.getActual()));
        assertTrue(compareResult.passed());
    }

    @Test
    void shouldGetOverviewWithYearMonthBalanceCalculatedPartially() throws IOException, URISyntaxException, JSONException {
        //arrange
        populateDatabaseFull(mongoClient);

        // calculate YearMonth Balance
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .queryParam("year", 2022)
            .queryParam("month", 3)
            .post("/budget/yearMonthBalance")
            .then()
            .statusCode(HttpStatus.SC_NO_CONTENT);

        final long mills2022May1 = ZonedDateTime
            .of(LocalDate.of(2022, 5, 1), LocalTime.NOON, ZoneId.of("Z"))
            .toInstant()
            .toEpochMilli();

        final JSONArray expectedJson = new JSONArray(JsonPath
            .from(TestUtils.loadFileAsString("json/overview.json"))
            .getList(""));

        //act + assert
        final JSONArray response = new JSONArray(givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("dateLong", mills2022May1)
            .get("/budget/overview/list/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .jsonPath()
            .getList(""));

        final JSONCompareResult compareResult = JSONCompare.compareJSON(expectedJson,
            response,
            JSONCompareMode.LENIENT);

        compareResult
            .getFieldUnexpected()
            .forEach(filedUnexpected -> System.out.println("Failed " + filedUnexpected.getField() + " Expected: " + filedUnexpected.getExpected() + " Actual: " + filedUnexpected.getActual()));
        compareResult
            .getFieldMissing()
            .forEach(fieldMissingFailure -> System.out.println("Failed " + fieldMissingFailure.getField() + " Expected: " + fieldMissingFailure.getExpected() + " Actual: " + fieldMissingFailure.getActual()));
        compareResult
            .getFieldFailures()
            .forEach(fieldComparisonFailure -> System.out.println("Failed " + fieldComparisonFailure.getField() + " Expected: " + fieldComparisonFailure.getExpected() + " Actual: " + fieldComparisonFailure.getActual()));
        assertTrue(compareResult.passed());
    }

}
