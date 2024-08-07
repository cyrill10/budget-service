package ch.bader.budget.boundary;

import ch.bader.budget.TestUtils;
import ch.bader.budget.boundary.dto.SaveScannedTransactionBoundaryDto;
import ch.bader.budget.type.AccountType;
import ch.bader.budget.type.ClosingProcessStatus;
import com.mongodb.client.MongoClient;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static ch.bader.budget.TestUtils.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
class ClosingProcessIT extends AbstractIT {

    @Inject
    MongoClient mongoClient;

    @Test
    void shouldLoadDb() {
        //arrange
        assertDoesNotThrow(() -> populateDatabaseFull(mongoClient));
    }

    @Test
    void whenProcessExists_shouldGetClosingProcess() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        //act + assert
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 4)
            .get("/budget/closingProcess")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(4))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.DONE.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()));
    }

    @Test
    void whenProcessDoesNotExists_shouldCreateAndGetClosingProcess() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        //act + assert
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 1)
            .get("/budget/closingProcess")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()));
    }

    @Test
    void whenCloseFileUpload_shouldCloseUploadStatus() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 1)
            .get("/budget/closingProcess")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()));

        //act + assert
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .queryParam("year", 2022)
            .queryParam("month", 1)
            .post("/budget/closingProcess/closeFileUpload")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.DONE.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()));


        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 1)
            .get("/budget/closingProcess")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.DONE.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()));
    }

    @Test
    void whenCloseTransferDetail_shouldCloseTransferDetailStatus() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 1)
            .get("/budget/closingProcess")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("transferStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()));

        //act + assert
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .queryParam("year", 2022)
            .queryParam("month", 1)
            .post("/budget/closingProcess/transfer/close")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("transferStatus.value", equalTo(ClosingProcessStatus.DONE.getValue()));


        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 1)
            .get("/budget/closingProcess")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("transferStatus.value", equalTo(ClosingProcessStatus.DONE.getValue()));
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ScannendTransactionEnglish {

        @Test
        @Order(1)
        void uploadFile() throws IOException, URISyntaxException {
            //arrange
            populateDatabaseFull(mongoClient);

            final File file = TestUtils.loadFile("creditCard/exampleTransactions.csv");

            //act + assert
            givenWithAuth()
                .contentType(ContentType.MULTIPART)
                .when()
                .multiPart("year", "2021")
                .multiPart("month", "11")
                .multiPart("file", file)
                .post("/budget/closingProcess")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false));
        }

        @Test
        @Order(2)
        void saveScannedTransactions_withoutThroughAccount() {

            //act + assert

            // read all scanned Transactions
            final JsonPath jsonPath = givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[15].description", equalTo("COOP-5734 BE SPITALGAS, BERN"))
                .body("[15].transactionCreated", equalTo(false))
                .body("[22].description", equalTo("DO IT + GARDEN MIGROS BER, BERN"))
                .body("[22].transactionCreated", equalTo(false))
                .body("[43].description", equalTo("COOP-5450 WORBLAUFEN A, WORBLAUFEN"))
                .body("[43].transactionCreated", equalTo(false))
                .body("[67].description", equalTo("NIESENBAHN AG, MÜLENEN"))
                .body("[67].transactionCreated", equalTo(false))
                .body("[81].description", equalTo("AMAVITA BAHNHOF BERN 1, BERN"))
                .body("[81].transactionCreated", equalTo(false))
                .body("[91].description", equalTo("ASIAN FOOD IMPORT GMBH, BERN"))
                .body("[91].transactionCreated", equalTo(false))
                .body("[115].description", equalTo("REINHARD AG, BERN"))
                .body("[115].transactionCreated", equalTo(false))
                .extract()
                .jsonPath();

            final String transactionId1 = jsonPath.getString("[15].id");
            final String transactionId2 = jsonPath.getString("[22].id");
            final String transactionId3 = jsonPath.getString("[43].id");
            final String transactionId4 = jsonPath.getString("[67].id");
            final String transactionId5 = jsonPath.getString("[81].id");
            final String transactionId6 = jsonPath.getString("[91].id");
            final String transactionId7 = jsonPath.getString("[115].id");
            final List<String> transactionIds = List.of(transactionId1,
                transactionId2,
                transactionId3,
                transactionId4,
                transactionId5,
                transactionId6,
                transactionId7);

            final SaveScannedTransactionBoundaryDto body = SaveScannedTransactionBoundaryDto
                .builder()
                .transactionIds(transactionIds)
                .creditedAccountId("62d172d93b2f355e5ceafb63")
                .debitedAccountId("62d172da3b2f355e5ceafb6b")
                .build();

            // create transactions from scannedtransactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .body(asJsonString(body))
                .when()
                .post("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK);


            // read scanned transactions again
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[15].transactionCreated", equalTo(true))
                .body("[22].transactionCreated", equalTo(true))
                .body("[43].transactionCreated", equalTo(true))
                .body("[67].transactionCreated", equalTo(true))
                .body("[81].transactionCreated", equalTo(true))
                .body("[91].transactionCreated", equalTo(true))
                .body("[115].transactionCreated", equalTo(true));

            // read transactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .param("date",
                    ZonedDateTime
                        .of(LocalDate.of(2021, 12, 1), LocalTime.NOON, ZoneId.of("Z"))
                        .toInstant()
                        .toEpochMilli())
                .get("/budget/transaction/list")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(16))
                .body("[10].description", equalTo("DO IT + GARDEN MIGROS BER, BERN"))
                .body("[10].budgetedAmount", equalTo(0))
                .body("[10].effectiveAmount", equalTo(28.5F))
                .body("[10].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[10].debitedAccount.name", equalTo("Lunch Cyrill"))
                .body("[10].creditedAccount.name", equalTo("Miles & More"))
                .body("[10].debitedAccount.underlyingAccount.name", equalTo("Prebudget"))
                .body("[10].debitedAccount.underlyingAccount.accountType.value",
                    equalTo(AccountType.PREBUDGETED.getValue()))
                .body("[15].description", equalTo("REINHARD AG, BERN"))
                .body("[15].budgetedAmount", equalTo(0))
                .body("[15].effectiveAmount", equalTo(10.8F))
                .body("[15].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[15].debitedAccount.name", equalTo("Lunch Cyrill"))
                .body("[15].creditedAccount.name", equalTo("Miles & More"))
                .body("[15].debitedAccount.underlyingAccount.name", equalTo("Prebudget"))
                .body("[15].debitedAccount.underlyingAccount.accountType.value",
                    equalTo(AccountType.PREBUDGETED.getValue()));

        }


        @Test
        @Order(3)
        void saveScannedTransactions_withThroughAccount() {

            //act + assert

            // read all scanned Transactions
            final JsonPath jsonPath = givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[16].description", equalTo("H & M, BERN"))
                .body("[16].transactionCreated", equalTo(false))
                .body("[77].description", equalTo("MOBILITY CAR SHARING, LUZERN"))
                .body("[77].transactionCreated", equalTo(false))
                .body("[105].description", equalTo("WWW.ZALANDO.CH, BERLIN"))
                .body("[105].transactionCreated", equalTo(false))
                .extract()
                .jsonPath();

            final String transactionId1 = jsonPath.getString("[16].id");
            final String transactionId2 = jsonPath.getString("[77].id");
            final String transactionId3 = jsonPath.getString("[105].id");
            final List<String> transactionIds = List.of(transactionId1, transactionId2, transactionId3);

            final SaveScannedTransactionBoundaryDto body = SaveScannedTransactionBoundaryDto
                .builder()
                .transactionIds(transactionIds)
                .creditedAccountId("62d172d93b2f355e5ceafb67")
                .debitedAccountId("62d172da3b2f355e5ceafb7a")
                .throughAccountId("62d172d93b2f355e5ceafb63")
                .build();

            // create transactions from scannedtransactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .body(asJsonString(body))
                .when()
                .post("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK);


            // read scanned transactions again
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[16].transactionCreated", equalTo(true))
                .body("[77].transactionCreated", equalTo(true))
                .body("[105].transactionCreated", equalTo(true));

            // read transactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .param("date",
                    ZonedDateTime
                        .of(LocalDate.of(2021, 12, 1), LocalTime.NOON, ZoneId.of("Z"))
                        .toInstant()
                        .toEpochMilli())
                .get("/budget/transaction/list")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(22))
                .body("[16].description", equalTo("H & M, BERN"))
                .body("[16].budgetedAmount", equalTo(0))
                .body("[16].effectiveAmount", equalTo(30.90F))
                .body("[16].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[16].debitedAccount.name", equalTo("Miles & More"))
                .body("[16].creditedAccount.name", equalTo("General Expenses"))
                .body("[16].debitedAccount.underlyingAccount.name", equalTo("Credit Cards"))
                .body("[16].debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.CREDIT.getValue()))
                .body("[17].description", equalTo("H & M, BERN"))
                .body("[17].budgetedAmount", equalTo(0))
                .body("[17].effectiveAmount", equalTo(30.90F))
                .body("[17].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[17].debitedAccount.name", equalTo("Furniture"))
                .body("[17].creditedAccount.name", equalTo("Miles & More"))
                .body("[17].debitedAccount.underlyingAccount.name", equalTo("Variable Costs"))
                .body("[17].debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.ALIEN.getValue()))
                .body("[21].description", equalTo("WWW.ZALANDO.CH, BERLIN"))
                .body("[21].budgetedAmount", equalTo(0))
                .body("[21].effectiveAmount", equalTo(-34.95F))
                .body("[21].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[21].debitedAccount.name", equalTo("Furniture"))
                .body("[21].creditedAccount.name", equalTo("Miles & More"))
                .body("[21].debitedAccount.underlyingAccount.name", equalTo("Variable Costs"))
                .body("[21].debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.ALIEN.getValue()));

        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ScannendTransactionGerman {

        @Test
        @Order(1)
        void uploadFile() throws IOException, URISyntaxException {
            //arrange
            populateDatabaseFull(mongoClient);

            final File file = TestUtils.loadFile("creditCard/exampleTransactionsGerman.csv");

            //act + assert
            givenWithAuth()
                .contentType(ContentType.MULTIPART)
                .when()
                .multiPart("year", "2021")
                .multiPart("month", "11")
                .multiPart("file", file)
                .post("/budget/closingProcess")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false));
        }

        @Test
        @Order(2)
        void saveScannedTransactions_withoutThroughAccount() {

            //act + assert

            // read all scanned Transactions
            final JsonPath jsonPath = givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[15].description", equalTo("COOP-5734 BE SPITALGAS, BERN"))
                .body("[15].transactionCreated", equalTo(false))
                .body("[22].description", equalTo("DO IT + GARDEN MIGROS BER, BERN"))
                .body("[22].transactionCreated", equalTo(false))
                .body("[43].description", equalTo("COOP-5450 WORBLAUFEN A, WORBLAUFEN"))
                .body("[43].transactionCreated", equalTo(false))
                .body("[67].description", equalTo("NIESENBAHN AG, MÜLENEN"))
                .body("[67].transactionCreated", equalTo(false))
                .body("[81].description", equalTo("AMAVITA BAHNHOF BERN 1, BERN"))
                .body("[81].transactionCreated", equalTo(false))
                .body("[91].description", equalTo("ASIAN FOOD IMPORT GMBH, BERN"))
                .body("[91].transactionCreated", equalTo(false))
                .body("[115].description", equalTo("REINHARD AG, BERN"))
                .body("[115].transactionCreated", equalTo(false))
                .extract()
                .jsonPath();

            final String transactionId1 = jsonPath.getString("[15].id");
            final String transactionId2 = jsonPath.getString("[22].id");
            final String transactionId3 = jsonPath.getString("[43].id");
            final String transactionId4 = jsonPath.getString("[67].id");
            final String transactionId5 = jsonPath.getString("[81].id");
            final String transactionId6 = jsonPath.getString("[91].id");
            final String transactionId7 = jsonPath.getString("[115].id");
            final List<String> transactionIds = List.of(transactionId1,
                transactionId2,
                transactionId3,
                transactionId4,
                transactionId5,
                transactionId6,
                transactionId7);

            final SaveScannedTransactionBoundaryDto body = SaveScannedTransactionBoundaryDto
                .builder()
                .transactionIds(transactionIds)
                .creditedAccountId("62d172d93b2f355e5ceafb63")
                .debitedAccountId("62d172da3b2f355e5ceafb6b")
                .build();

            // create transactions from scannedtransactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .body(asJsonString(body))
                .when()
                .post("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK);


            // read scanned transactions again
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[15].transactionCreated", equalTo(true))
                .body("[22].transactionCreated", equalTo(true))
                .body("[43].transactionCreated", equalTo(true))
                .body("[67].transactionCreated", equalTo(true))
                .body("[81].transactionCreated", equalTo(true))
                .body("[91].transactionCreated", equalTo(true))
                .body("[115].transactionCreated", equalTo(true));

            // read transactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .param("date",
                    ZonedDateTime
                        .of(LocalDate.of(2021, 12, 1), LocalTime.NOON, ZoneId.of("Z"))
                        .toInstant()
                        .toEpochMilli())
                .get("/budget/transaction/list")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(16))
                .body("[10].description", equalTo("DO IT + GARDEN MIGROS BER, BERN"))
                .body("[10].budgetedAmount", equalTo(0))
                .body("[10].effectiveAmount", equalTo(28.5F))
                .body("[10].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[10].debitedAccount.name", equalTo("Lunch Cyrill"))
                .body("[10].creditedAccount.name", equalTo("Miles & More"))
                .body("[10].debitedAccount.underlyingAccount.name", equalTo("Prebudget"))
                .body("[10].debitedAccount.underlyingAccount.accountType.value",
                    equalTo(AccountType.PREBUDGETED.getValue()))
                .body("[15].description", equalTo("REINHARD AG, BERN"))
                .body("[15].budgetedAmount", equalTo(0))
                .body("[15].effectiveAmount", equalTo(10.8F))
                .body("[15].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[15].debitedAccount.name", equalTo("Lunch Cyrill"))
                .body("[15].creditedAccount.name", equalTo("Miles & More"))
                .body("[15].debitedAccount.underlyingAccount.name", equalTo("Prebudget"))
                .body("[15].debitedAccount.underlyingAccount.accountType.value",
                    equalTo(AccountType.PREBUDGETED.getValue()));

        }


        @Test
        @Order(3)
        void saveScannedTransactions_withThroughAccount() {

            //act + assert

            // read all scanned Transactions
            final JsonPath jsonPath = givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[16].description", equalTo("H & M, BERN"))
                .body("[16].transactionCreated", equalTo(false))
                .body("[77].description", equalTo("MOBILITY CAR SHARING, LUZERN"))
                .body("[77].transactionCreated", equalTo(false))
                .body("[105].description", equalTo("WWW.ZALANDO.CH, BERLIN"))
                .body("[105].transactionCreated", equalTo(false))
                .extract()
                .jsonPath();

            final String transactionId1 = jsonPath.getString("[16].id");
            final String transactionId2 = jsonPath.getString("[77].id");
            final String transactionId3 = jsonPath.getString("[105].id");
            final List<String> transactionIds = List.of(transactionId1, transactionId2, transactionId3);

            final SaveScannedTransactionBoundaryDto body = SaveScannedTransactionBoundaryDto
                .builder()
                .transactionIds(transactionIds)
                .creditedAccountId("62d172d93b2f355e5ceafb67")
                .debitedAccountId("62d172da3b2f355e5ceafb7a")
                .throughAccountId("62d172d93b2f355e5ceafb63")
                .build();

            // create transactions from scannedtransactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .body(asJsonString(body))
                .when()
                .post("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK);


            // read scanned transactions again
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("year", 2021)
                .queryParam("month", 11)
                .get("/budget/closingProcess/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(127))
                .body("[0].date", equalTo("2022-04-20"))
                .body("[0].description", equalTo("COOP-1990 BE C.RYFFLIH, BERN"))
                .body("[0].amount", equalTo(27.9F))
                .body("[0].cardType", equalTo("VISA"))
                .body("[0].transactionCreated", equalTo(false))
                .body("[126].date", equalTo("2022-05-20"))
                .body("[126].description", equalTo("TRAVEL COMFORT PLUS"))
                .body("[126].amount", equalTo(13.5F))
                .body("[126].cardType", equalTo("AMEX"))
                .body("[126].transactionCreated", equalTo(false))
                .body("[16].transactionCreated", equalTo(true))
                .body("[77].transactionCreated", equalTo(true))
                .body("[105].transactionCreated", equalTo(true));

            // read transactions
            givenWithAuth()
                .contentType(ContentType.JSON)
                .when()
                .param("date",
                    ZonedDateTime
                        .of(LocalDate.of(2021, 12, 1), LocalTime.NOON, ZoneId.of("Z"))
                        .toInstant()
                        .toEpochMilli())
                .get("/budget/transaction/list")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", equalTo(22))
                .body("[16].description", equalTo("H & M, BERN"))
                .body("[16].budgetedAmount", equalTo(0))
                .body("[16].effectiveAmount", equalTo(30.90F))
                .body("[16].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[16].debitedAccount.name", equalTo("Miles & More"))
                .body("[16].creditedAccount.name", equalTo("General Expenses"))
                .body("[16].debitedAccount.underlyingAccount.name", equalTo("Credit Cards"))
                .body("[16].debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.CREDIT.getValue()))
                .body("[17].description", equalTo("H & M, BERN"))
                .body("[17].budgetedAmount", equalTo(0))
                .body("[17].effectiveAmount", equalTo(30.90F))
                .body("[17].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[17].debitedAccount.name", equalTo("Furniture"))
                .body("[17].creditedAccount.name", equalTo("Miles & More"))
                .body("[17].debitedAccount.underlyingAccount.name", equalTo("Variable Costs"))
                .body("[17].debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.ALIEN.getValue()))
                .body("[21].description", equalTo("WWW.ZALANDO.CH, BERLIN"))
                .body("[21].budgetedAmount", equalTo(0))
                .body("[21].effectiveAmount", equalTo(-34.95F))
                .body("[21].date", equalTo("2021-12-10T12:00:00Z"))
                .body("[21].debitedAccount.name", equalTo("Furniture"))
                .body("[21].creditedAccount.name", equalTo("Miles & More"))
                .body("[21].debitedAccount.underlyingAccount.name", equalTo("Variable Costs"))
                .body("[21].debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.ALIEN.getValue()));

        }
    }


    @Test
    void shouldGetTransferDetail() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        //act + assert
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 1)
            .get("/budget/closingProcess")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("year", equalTo(2022))
            .body("month", equalTo(1))
            .body("uploadStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("manualEntryStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()))
            .body("transferStatus.value", equalTo(ClosingProcessStatus.NEW.getValue()));


        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("year", 2022)
            .param("month", 1)
            .get("/budget/closingProcess/transfer/details")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("[0].accountName", equalTo("Cyrill Saving"))
            .body("[0].transferAmount", equalTo(220.0F))
            .body("[1].accountName", equalTo("Lyka Saving"))
            .body("[1].transferAmount", equalTo(-3171.7F));
    }
}
