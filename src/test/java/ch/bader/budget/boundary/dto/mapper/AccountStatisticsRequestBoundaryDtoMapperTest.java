package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.AccountStatisticsRequestBoundaryDto;
import ch.bader.budget.domain.statistics.AccountStatisticsRequest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class AccountStatisticsRequestBoundaryDtoMapperTest {

    @Inject
    AccountStatisticsRequestBoundaryDtoMapperImpl sut;

    @Test
    void mapFromBoundaryDto() {
        //-- arrange
        final AccountStatisticsRequestBoundaryDto input = createInputDto();

        //-- act
        final AccountStatisticsRequest result = sut.mapFromBoundaryDto(input);

        //-- assert
        assertFullyMappedDto(result, input);
    }

    private void assertFullyMappedDto(final AccountStatisticsRequest result,
                                      final AccountStatisticsRequestBoundaryDto input) {
        assertEquals(input.getVirtualAccountId(), result.getVirtualAccountId());
        assertEquals(input.getRealAccountId(), result.getRealAccountId());
        assertEquals(input.getToYear(), result.getTo().getYear());
        assertEquals(input.getToMonth(), result.getTo().getMonthValue());
        assertEquals(input.getFromYear(), result.getFrom().getYear());
        assertEquals(input.getFromMonth(), result.getFrom().getMonthValue());
    }

    private AccountStatisticsRequestBoundaryDto createInputDto() {
        return AccountStatisticsRequestBoundaryDto
            .builder()
            .virtualAccountId(UUID.randomUUID().toString())
            .realAccountId(UUID.randomUUID().toString())
            .fromMonth(1)
            .fromYear(2023)
            .toMonth(5)
            .toYear(2024)
            .build();
    }
}