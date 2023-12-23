package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.RealAccountBoundaryDto;
import ch.bader.budget.boundary.dto.TransactionBoundaryDto;
import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.boundary.dto.VirtualAccountBoundaryDto;
import ch.bader.budget.type.AccountType;
import ch.bader.budget.type.PaymentStatus;
import ch.bader.budget.type.PaymentType;
import ch.bader.budget.type.TransactionIndication;
import com.mongodb.client.MongoClient;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.bader.budget.TestUtils.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
class TransactionIT extends AbstractIT {

    @Inject
    MongoClient mongoClient;

    @Test
    void shouldLoadDb() {
        //arrange
        assertDoesNotThrow(() -> populateDatabaseFull(mongoClient));
    }

    @Test
    void shouldAddTransaction() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        final RealAccountBoundaryDto underlyingAccount = RealAccountBoundaryDto.builder()
                                                                               //4
                                                                               .id("62aa325d44240b637b194243")
                                                                               .name("Credit Cards")
                                                                               .accountType(ValueEnumBoundaryDto
                                                                                   .builder()
                                                                                   .value(AccountType.CREDIT.getValue())
                                                                                   .build())
                                                                               .build();

        final VirtualAccountBoundaryDto virtualAccount = VirtualAccountBoundaryDto.builder()
                                                                                  //2
                                                                                  .id("62d172d93b2f355e5ceafb63")
                                                                                  .name("Bonviva")
                                                                                  .isDeleted(false)
                                                                                  .balance(BigDecimal.ZERO)
                                                                                  .underlyingAccount(underlyingAccount)
                                                                                  .build();

        final TransactionBoundaryDto input = TransactionBoundaryDto
            .builder()
            .creditedAccount(virtualAccount)
            .debitedAccount(virtualAccount)
            .description("Test Transaction")
            .date(ZonedDateTime.now())
            .paymentType(ValueEnumBoundaryDto.builder().value(PaymentType.DEPOSIT.getValue()).build())
            .paymentStatus(ValueEnumBoundaryDto.builder().value(PaymentStatus.PAID.getValue()).build())
            .indication(ValueEnumBoundaryDto.builder().value(TransactionIndication.EXPECTED.getValue()).build())
            .budgetedAmount(BigDecimal.ONE)
            .effectiveAmount(BigDecimal.TEN)
            .build();


        givenWithAuth()
            .contentType(ContentType.JSON)
            .body(asJsonString(input))
            .when()
            .post("/budget/transaction/add/")
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("description", equalTo("Test Transaction"))
            .body("id", isA(String.class))
            .body("budgetedAmount", equalTo(1))
            .body("effectiveAmount", equalTo(10))
            .body("debitedAccount.name", equalTo("Bonviva"))
            .body("creditedAccount.name", equalTo("Bonviva"))
            .body("debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.CREDIT.getValue()));
    }

    @Test
    void shouldUpdateTransaction() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        final RealAccountBoundaryDto underlyingAccount = RealAccountBoundaryDto.builder()
                                                                               //1
                                                                               .id("62d172d23b2f355e5ceafb5a")
                                                                               .name("Checking")
                                                                               .accountType(ValueEnumBoundaryDto
                                                                                   .builder()
                                                                                   .value(AccountType.CHECKING.getValue())
                                                                                   .build())
                                                                               .build();

        final VirtualAccountBoundaryDto virtualAccountChecking = VirtualAccountBoundaryDto.builder()
                                                                                          //4
                                                                                          .id("62d172d93b2f355e5ceafb65")
                                                                                          .name("Checking")
                                                                                          .isDeleted(false)
                                                                                          .balance(BigDecimal.ZERO)
                                                                                          .underlyingAccount(
                                                                                              underlyingAccount)
                                                                                          .build();

        final VirtualAccountBoundaryDto virtualAccountHealth = VirtualAccountBoundaryDto.builder()
                                                                                        //7
                                                                                        .id("62d172d93b2f355e5ceafb68")
                                                                                        .name("Health Savings")
                                                                                        .isDeleted(false)
                                                                                        .balance(BigDecimal.ZERO)
                                                                                        .underlyingAccount(
                                                                                            underlyingAccount)
                                                                                        .build();


        final TransactionBoundaryDto input = TransactionBoundaryDto.builder()
                                                                   //294
                                                                   .id("62d172e63b2f355e5ceafc9b")
                                                                   .creditedAccount(virtualAccountChecking)
                                                                   .debitedAccount(virtualAccountHealth)
                                                                   .description("Health Saving")
                                                                   .date(ZonedDateTime.of(LocalDate.of(2022, 2, 25),
                                                                       LocalTime.NOON,
                                                                       ZoneId.of("Z")))
                                                                   .paymentType(ValueEnumBoundaryDto
                                                                       .builder()
                                                                       .value(PaymentType.DEPOSIT.getValue())
                                                                       .build())
                                                                   .paymentStatus(ValueEnumBoundaryDto
                                                                       .builder()
                                                                       .value(PaymentStatus.PAID.getValue())
                                                                       .build())
                                                                   .indication(ValueEnumBoundaryDto
                                                                       .builder()
                                                                       .value(TransactionIndication.EXPECTED.getValue())
                                                                       .build())
                                                                   .budgetedAmount(BigDecimal.valueOf(400))
                                                                   .effectiveAmount(BigDecimal.valueOf(500))
                                                                   .build();

        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .body(asJsonString(input))
            .when()
            .put("/budget/transaction/update")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("description", equalTo("Health Saving"))
            //294
            .body("id", equalTo("62d172e63b2f355e5ceafc9b"))
            .body("budgetedAmount", equalTo(400))
            .body("effectiveAmount", equalTo(500))
            .body("debitedAccount.name", equalTo("Health Savings"))
            .body("creditedAccount.name", equalTo("Checking"))
            .body("debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.CHECKING.getValue()));


        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            //294
            .param("id", "62d172e63b2f355e5ceafc9b")
            .get("/budget/transaction/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("description", equalTo("Health Saving"))
            //294
            .body("id", equalTo("62d172e63b2f355e5ceafc9b"))
            .body("budgetedAmount", equalTo(400))
            .body("effectiveAmount", equalTo(500))
            .body("debitedAccount.name", equalTo("Health Savings"))
            .body("creditedAccount.name", equalTo("Checking"))
            .body("debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.CHECKING.getValue()));

    }


    @Test
    void shouldGetTransaction() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            //294
            .param("id", "62d172e63b2f355e5ceafc9b")
            .get("/budget/transaction/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("description", equalTo("Health Saving"))
            //294
            .body("id", equalTo("62d172e63b2f355e5ceafc9b"))
            .body("budgetedAmount", equalTo(400F))
            .body("effectiveAmount", equalTo(400F))
            .body("debitedAccount.name", equalTo("Health Savings"))
            .body("creditedAccount.name", equalTo("Checking"))
            .body("debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.CHECKING.getValue()));

    }

    @Test
    void shouldDeleteTransaction() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            //294
            .param("transactionId", "62d172e63b2f355e5ceafc9b")
            .delete("/budget/transaction/delete")
            .then()
            .statusCode(HttpStatus.SC_OK);

        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            //294
            .param("id", "62d172e63b2f355e5ceafc9b")
            .get("/budget/transaction/")
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void shouldDuplicateTransaction() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);

        final RealAccountBoundaryDto underlyingAccount = RealAccountBoundaryDto.builder()
                                                                               //1
                                                                               .id("62d172d23b2f355e5ceafb5a")
                                                                               .name("Checking")
                                                                               .accountType(ValueEnumBoundaryDto
                                                                                   .builder()
                                                                                   .value(AccountType.CHECKING.getValue())
                                                                                   .build())
                                                                               .build();

        final VirtualAccountBoundaryDto virtualAccountChecking = VirtualAccountBoundaryDto.builder()
                                                                                          //4
                                                                                          .id("62d172d93b2f355e5ceafb65")
                                                                                          .name("Checking")
                                                                                          .isDeleted(false)
                                                                                          .balance(BigDecimal.ZERO)
                                                                                          .underlyingAccount(
                                                                                              underlyingAccount)
                                                                                          .build();

        final VirtualAccountBoundaryDto virtualAccountHealth = VirtualAccountBoundaryDto.builder()
                                                                                        //7
                                                                                        .id("62d172d93b2f355e5ceafb68")
                                                                                        .name("Health Savings")
                                                                                        .isDeleted(false)
                                                                                        .balance(BigDecimal.ZERO)
                                                                                        .underlyingAccount(
                                                                                            underlyingAccount)
                                                                                        .build();


        final TransactionBoundaryDto input = TransactionBoundaryDto.builder()
                                                                   //294
                                                                   .id("62d172e63b2f355e5ceafc9b")
                                                                   .creditedAccount(virtualAccountChecking)
                                                                   .debitedAccount(virtualAccountHealth)
                                                                   .description("Health Saving")
                                                                   .date(ZonedDateTime.of(LocalDate.of(2022, 2, 25),
                                                                       LocalTime.NOON,
                                                                       ZoneId.of("Z")))
                                                                   .paymentType(ValueEnumBoundaryDto
                                                                       .builder()
                                                                       .value(PaymentType.DEPOSIT.getValue())
                                                                       .build())
                                                                   .paymentStatus(ValueEnumBoundaryDto
                                                                       .builder()
                                                                       .value(PaymentStatus.PAID.getValue())
                                                                       .build())
                                                                   .indication(ValueEnumBoundaryDto
                                                                       .builder()
                                                                       .value(TransactionIndication.EXPECTED.getValue())
                                                                       .build())
                                                                   .budgetedAmount(BigDecimal.valueOf(400))
                                                                   .effectiveAmount(BigDecimal.valueOf(400))
                                                                   .build();
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .body(asJsonString(input))
            .when()
            .post("/budget/transaction/dublicate")
            .then()
            .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    void shouldGetAllTransactionsForMonth() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("date",
                ZonedDateTime.of(LocalDate.of(2022, 6, 1), LocalTime.NOON, ZoneId.of("Z")).toInstant().toEpochMilli())
            .get("/budget/transaction/list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(169))
            .body("[0].description", equalTo("Going out"))
            .body("[0].budgetedAmount", equalTo(300F))
            .body("[0].effectiveAmount", equalTo(0F))
            .body("[0].debitedAccount.name", equalTo("Going Out"))
            .body("[0].creditedAccount.name", equalTo("Miles & More"))
            .body("[0].debitedAccount.underlyingAccount.accountType.value", equalTo(AccountType.PREBUDGETED.getValue()))
            .body("[10].description", equalTo("RACE INN, ROGGWIL BE"))
            .body("[10].budgetedAmount", equalTo(0F))
            .body("[10].effectiveAmount", equalTo(5F))
            .body("[10].date", equalTo("2022-06-01T12:00:00Z"))
            .body("[10].debitedAccount.name", equalTo("Lunch Cyrill"))
            .body("[10].creditedAccount.name", equalTo("Miles & More"))
            .body("[10].debitedAccount.underlyingAccount.name", equalTo("Prebudget"))
            .body("[10].debitedAccount.underlyingAccount.accountType.value",
                equalTo(AccountType.PREBUDGETED.getValue()));

    }

    @Test
    void shouldGetAllTransactionsForMonthAndVirtualAccount() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("date",
                ZonedDateTime.of(LocalDate.of(2022, 5, 1), LocalTime.NOON, ZoneId.of("Z")).toInstant().toEpochMilli())
            //2
            .param("accountId", "62d172d93b2f355e5ceafb63")
            .get("/budget/transaction/listByMonthAndVirtualAccount")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(141))
            .body("[0].name", equalTo("Before"))
            .body("[0].balance", equalTo(0F))
            .body("[0].amount", equalTo(0))
            .body("[0].budgetedBalance", equalTo(0F))
            .body("[0].budgetedAmount", equalTo(0))
            .body("[0].id", equalTo("0"))

            .body("[131].name", equalTo("Creditcard Payment"))
            .body("[131].balance", equalTo(23.50F))
            .body("[131].amount", equalTo(2435.25F))
            .body("[131].budgetedBalance", equalTo(23.50F))
            .body("[131].budgetedAmount", equalTo(1923.5F))

            .body("[87].name", equalTo("DOMINO'S PIZZA GMBH, GLATTBRUGG"))
            .body("[87].balance", equalTo(-2054.40F))
            .body("[87].amount", equalTo(-29.9F))
            .body("[87].budgetedBalance", equalTo(-2054.40F))
            .body("[87].budgetedAmount", equalTo(0F))
            //590

            .body("[139].name", equalTo("After"))
            .body("[139].balance", equalTo(0F))
            .body("[139].amount", equalTo(0))
            .body("[139].budgetedBalance", equalTo(0F))
            .body("[139].budgetedAmount", equalTo(0))
            .body("[139].id", equalTo("2147483646"))

            .body("[140].name", equalTo("In/Out"))
            .body("[140].balance", equalTo(-3760.35F))
            .body("[140].budgetedBalance", equalTo(-3760.35F))
            .body("[140].id", equalTo("2147483647"));
    }

    @Test
    void shouldGetAllTransactionsForMonthAndRealAccount() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("date",
                ZonedDateTime.of(LocalDate.of(2022, 6, 1), LocalTime.NOON, ZoneId.of("Z")).toInstant().toEpochMilli())
            //1
            .param("accountId", "62d172d23b2f355e5ceafb5a")
            .get("/budget/transaction/listByMonthAndRealAccount")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(32))
            .body("[0].name", equalTo("Before"))
            .body("[0].balance", equalTo(2337.54F))
            .body("[0].amount", equalTo(0))
            .body("[0].budgetedBalance", equalTo(2337.54F))
            .body("[0].budgetedAmount", equalTo(0))
            .body("[0].id", equalTo("0"))

            .body("[12].name", equalTo("Health insurance"))
            .body("[12].balance", equalTo(-2513.31F))
            .body("[12].amount", equalTo(-794.15F))
            .body("[12].budgetedBalance", equalTo(-2513.31F))
            .body("[12].budgetedAmount", equalTo(-794.15F))

            .body("[10].name", equalTo("Rent"))
            .body("[10].balance", equalTo(-1519.06F))
            .body("[10].amount", equalTo(-1356.0F))
            .body("[10].budgetedBalance", equalTo(-1519.06F))
            .body("[10].budgetedAmount", equalTo(-1356.0F))

            .body("[31].name", equalTo("After"))
            .body("[31].balance", equalTo(2361.84F))
            .body("[31].amount", equalTo(0))
            .body("[31].budgetedBalance", equalTo(2361.84F))
            .body("[31].budgetedAmount", equalTo(0))
            .body("[31].id", equalTo("2147483646"));
    }

    @Test
    void shouldGetPaymentTyps() {
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .get("/budget/transaction/type/list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(3))
            .body("[0].name", equalTo("Deposit"))
            .body("[0].value", equalTo(1))
            .body("[1].name", equalTo("Standing Order"))
            .body("[1].value", equalTo(2))
            .body("[2].name", equalTo("e-bill"))
            .body("[2].value", equalTo(3));
    }

    @Test
    void shouldGetIndicationTyps() {
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .get("/budget/transaction/indication/list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(2))
            .body("[0].name", equalTo("E"))
            .body("[0].value", equalTo(1))
            .body("[1].name", equalTo("U"))
            .body("[1].value", equalTo(2));
    }

    @Test
    void shouldGetStatusTyps() {
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .get("/budget/transaction/status/list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(3))
            .body("[0].name", equalTo("open"))
            .body("[0].value", equalTo(1))
            .body("[1].name", equalTo("set up"))
            .body("[1].value", equalTo(2))
            .body("[2].name", equalTo("paid"))
            .body("[2].value", equalTo(3));
    }
}
