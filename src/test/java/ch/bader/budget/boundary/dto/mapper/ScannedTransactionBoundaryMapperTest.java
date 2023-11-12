package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ScannedTransactionBoundaryDto;
import ch.bader.budget.domain.ScannedTransaction;
import ch.bader.budget.type.CardType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ScannedTransactionBoundaryMapperTest {

    @Inject
    ScannedTransactionBoundaryDtoMapperImpl sut;

    @Test
    void shouldMapScannedTransactionToDto() {
        //given

        final ScannedTransaction domain = ScannedTransaction.builder()
                                                            .id("id")
                                                            .amount(BigDecimal.TEN)
                                                            .transactionCreated(true)
                                                            .cardType(CardType.VISA)
                                                            .date(LocalDate.now())
                                                            .description("scannedTransactionDesc")
                                                            .yearMonth(YearMonth.of(2022, 1))
                                                            .build();

        //when
        final ScannedTransactionBoundaryDto dto = sut.mapToDto(domain);

        //then
        assertNotNull(dto);
        assertEquals(domain.getId(), dto.getId());
        assertEquals(domain.getAmount(), dto.getAmount());
        assertEquals(domain.getTransactionCreated(), dto.getTransactionCreated());
        assertEquals(domain.getCardType().name(), dto.getCardType());
        assertEquals(domain.getDate(), dto.getDate());
        assertEquals(domain.getDescription(), dto.getDescription());
    }


}