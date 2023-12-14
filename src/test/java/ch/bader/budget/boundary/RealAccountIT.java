package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.RealAccountBoundaryDto;
import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.AccountType;
import com.mongodb.client.MongoClient;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static ch.bader.budget.TestUtils.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
class RealAccountIT extends AbstractIT {

    @Inject
    MongoClient mongoClient;

    @Test
    void shouldLoadDb() {
        //arrange
        assertDoesNotThrow(() -> populateDatabaseFull(mongoClient));
    }

    @Test
    void shouldAddAccount() {
        final RealAccountBoundaryDto input = RealAccountBoundaryDto
            .builder()
            .name("TestAccount")
            .accountType(ValueEnumBoundaryDto.builder().value(AccountType.CHECKING.getValue()).build())
            .build();

        givenWithAuth()
            .contentType(ContentType.JSON)
            .body(asJsonString(input))
            .when()
            .post("/budget/realAccount/add/")
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("name", equalTo("TestAccount"))
            .body("id", isA(String.class))
            .body("accountType.value", equalTo(AccountType.CHECKING.getValue()));
    }

    @Test
    void shouldUpdateAccount() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        final RealAccountBoundaryDto input = RealAccountBoundaryDto
            .builder()
            .id("62dbd0b9a513386b4764f074")
            .name("TestAccount2")
            .accountType(ValueEnumBoundaryDto.builder().value(AccountType.CREDIT.getValue()).build())
            .build();

        givenWithAuth()
            .contentType(ContentType.JSON)
            .body(asJsonString(input))
            .when()
            .put("/budget/realAccount/update")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("name", equalTo("TestAccount2"))
            .body("id", equalTo("62dbd0b9a513386b4764f074"))
            .body("accountType.value", equalTo(AccountType.CREDIT.getValue()));

        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .param("id", "62dbd0b9a513386b4764f074")
            .get("/budget/realAccount")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("name", equalTo("TestAccount2"))
            .body("id", equalTo("62dbd0b9a513386b4764f074"))
            .body("accountType.value", equalTo(AccountType.CREDIT.getValue()));
    }


    @Test
    void shouldGetAccount() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            //1
            .param("id", "62d172d23b2f355e5ceafb5a")
            .get("/budget/realAccount/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("name", equalTo("Checking"))
            //1
            .body("id", equalTo("62d172d23b2f355e5ceafb5a"))
            .body("accountType.value", equalTo(AccountType.CHECKING.getValue()));
    }

    @Test
    void shouldGetAllAccounts() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .get("/budget/realAccount/list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(9))
            .body("[0].realAccount.name", equalTo("Checking"))
            .body("[0].virtualAccounts.size()", equalTo(5))
            .body("[1].virtualAccounts[1].name", equalTo("Libera Lyka"))
            .body("[8].realAccount.name", equalTo("Prebudget"))
            .body("[8].virtualAccounts.size()", equalTo(5))
            .body("[8].virtualAccounts[0].name", equalTo("Going Out"))
            .body("[8].virtualAccounts[0].underlyingAccount.accountType.value", equalTo(5));
    }

    @Test
    void shouldGetAllAccountTyps() {
        //act
        givenWithAuth()
            .contentType(ContentType.JSON)
            .when()
            .get("/budget/realAccount/type/list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$.size()", equalTo(5))
            .body("[0].name", equalTo("Checking"))
            .body("[0].value", equalTo(1))
            .body("[1].name", equalTo("Saving"))
            .body("[1].value", equalTo(2))
            .body("[2].name", equalTo("Credit"))
            .body("[2].value", equalTo(3))
            .body("[3].name", equalTo("Alien"))
            .body("[3].value", equalTo(4))
            .body("[4].name", equalTo("Prebudgeted"))
            .body("[4].value", equalTo(5));
    }
}
