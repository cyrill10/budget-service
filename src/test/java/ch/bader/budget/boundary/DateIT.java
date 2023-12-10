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
        final LocalDate firstDay = LocalDate.of(2021, 12, 1);
        final LocalDate expectedLastDate = LocalDate.now().plusYears(1).withDayOfMonth(1);

        final long diff = ChronoUnit.MONTHS.between(firstDay, expectedLastDate);
        // When & Then
        final JsonPath response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/budget/date/month/list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("[0]", equalTo(firstDay.toString()))
            .extract()
            .jsonPath();

        final List<String> list = response.get("$");
        assertEquals(diff, list.size());
    }
}
