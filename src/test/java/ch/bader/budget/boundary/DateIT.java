package ch.bader.budget.boundary;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
class DateIT {

    @Test
    void shouldReturnMonths() {
        // Given
        LocalDate firstDay = LocalDate.of(2022, 12, 1);
        LocalDate expectedLastDate = LocalDate.now().plusYears(1).withDayOfMonth(1);

        long diff = ChronoUnit.MONTHS.between(
            firstDay,
            expectedLastDate);
        // When & Then
        JsonPath response = given().contentType(ContentType.JSON)
                                   .when()
                                   .get("/budget/date/month/list")
                                   .then()
                                   .statusCode(HttpStatus.SC_OK)
                                   .body("[0]", equalTo(firstDay.toString()))
                                   .extract().jsonPath();

        List<String> list = response.get("$");
        assertEquals(diff, list.size());
    }
}
