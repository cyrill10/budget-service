package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.RealAccountBoundaryDto;
import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.boundary.dto.VirtualAccountBoundaryDto;
import ch.bader.budget.type.AccountType;
import com.mongodb.client.MongoClient;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import static ch.bader.budget.TestUtils.asJsonString;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
class VirtualAccountIT extends AbstractIT {

    @Inject
    MongoClient mongoClient;

    @Test
    void shouldLoadDb() {
        //arrange
        assertDoesNotThrow(() -> populateDatabaseFull(mongoClient));
    }

    @Test
    void shouldAddAccount() {
        final RealAccountBoundaryDto underlyingAccount = RealAccountBoundaryDto
            .builder()
            //4
            .id("62a50222ce7b3719fa1aac5f")
            .name("Credit Cards")
            .accountType(ValueEnumBoundaryDto.builder()
                                             .value(AccountType.CREDIT.getValue())
                                             .build())
            .build();

        final VirtualAccountBoundaryDto input = VirtualAccountBoundaryDto.builder()
                                                                         .name("TestVirtual")
                                                                         .underlyingAccount(underlyingAccount)
                                                                         .build();

        given().contentType(ContentType.JSON)
               .body(asJsonString(input))
               .when()
               .post("/budget/virtualAccount/add/")
               .then()
               .statusCode(HttpStatus.SC_CREATED)
               .body("name", equalTo("TestVirtual"))
               .body("id", isA(String.class))
               .body("balance", equalTo(0))
               .body("isDeleted", equalTo(false))
               //4
               .body("underlyingAccount.id", equalTo("62a50222ce7b3719fa1aac5f"));
    }

    @Test
    void shouldUpdateAccount() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        final RealAccountBoundaryDto underlyingAccount = RealAccountBoundaryDto
            .builder()
            //5
            .id("62d172d33b2f355e5ceafb5e")
            .name("Prebudgeted")
            .accountType(ValueEnumBoundaryDto.builder()
                                             .value(AccountType.PREBUDGETED.getValue())
                                             .build())
            .build();

        final VirtualAccountBoundaryDto input = VirtualAccountBoundaryDto
            .builder()
            //8
            .id("62d172da3b2f355e5ceafb69")
            .name("TestVirtual")
            .underlyingAccount(underlyingAccount)
            .balance(BigDecimal.ZERO)
            .isDeleted(false)
            .build();

        given().contentType(ContentType.JSON)
               .body(asJsonString(input))
               .when()
               .put("/budget/virtualAccount/update")
               .then()
               .statusCode(HttpStatus.SC_OK)
               .body("name", equalTo("TestVirtual"))
               //8
               .body("id", equalTo("62d172da3b2f355e5ceafb69"))
               .body("balance", equalTo(0))
               .body("isDeleted", equalTo(false))
               //5
               .body("underlyingAccount.id", equalTo("62d172d33b2f355e5ceafb5e"));


        given().contentType(ContentType.JSON)
               .when()
               //8
               .param("id", "62d172da3b2f355e5ceafb69")
               .get("/budget/virtualAccount/")
               .then()
               .statusCode(HttpStatus.SC_OK)
               .body("name", equalTo("TestVirtual"))
               //8
               .body("id", equalTo("62d172da3b2f355e5ceafb69"))
               .body("balance", equalTo(0))
               .body("isDeleted", equalTo(false))
               //5
               .body("underlyingAccount.id", equalTo("62d172d33b2f355e5ceafb5e"));
    }


    @Test
    void shouldGetAccount() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        given().contentType(ContentType.JSON)
               .when()
               //6
               .param("id", "62d172d93b2f355e5ceafb67")
               .get("/budget/virtualAccount/")
               .then()
               .statusCode(HttpStatus.SC_OK)
               .body("name", equalTo("General Expenses"))
               //6
               .body("id", equalTo("62d172d93b2f355e5ceafb67"))
               //1
               .body("underlyingAccount.id", equalTo("62d172d23b2f355e5ceafb5a"));

    }

    @Test
    void shouldGetAllAccounts() throws IOException, URISyntaxException {
        //arrange
        populateDatabaseFull(mongoClient);
        //act
        given().contentType(ContentType.JSON)
               .when()
               .get("/budget/virtualAccount/list")
               .then()
               .statusCode(HttpStatus.SC_OK)
               .body("$.size()", equalTo(37))
               //3
               .body("[0].id", equalTo("62d172d93b2f355e5ceafb64"))
               .body("[0].name", equalTo("Bonviva"))
               //4
               .body("[0].underlyingAccount.id", equalTo("62d172d33b2f355e5ceafb5d"));
    }

}
