package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.AccountStatisticsBoundaryDto;
import ch.bader.budget.domain.statistics.AccountStatistics;
import ch.bader.budget.type.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class AccountStatisticsBoundaryDtoMapperTest {

    @Inject
    AccountStatisticsBoundaryDtoMapperImpl sut;

    @Test
    void mapToBoundaryDto() {
        //-- arrange
        final AccountStatistics input = createInputEntity();

        //-- act
        final AccountStatisticsBoundaryDto result = sut.mapToBoundaryDto(input);

        //-- assert
        assertFullyMappedDto(result, input);
    }

    private void assertFullyMappedDto(final AccountStatisticsBoundaryDto result, final AccountStatistics input) {
        assertEquals(input.getId(), result.getId());
        assertEquals(input.getAccountType().getValue(), result.getAccountType().getValue());
        assertEquals(input.getName(), result.getName());
        assertEquals(input.getBalanceIngoing(), result.getBalanceIngoing());
        assertEquals(input.getBalanceOutgoing(), result.getBalanceOutgoing());
    }

    private AccountStatistics createInputEntity() {
        return AccountStatistics
            .builder()
            .id(UUID.randomUUID().toString())
            .accountType(AccountType.PREBUDGETED)
            .name("AccountName")
            .balanceIngoing(BigDecimal.ZERO)
            .balanceOutgoing(BigDecimal.TEN)
            .build();
    }

}