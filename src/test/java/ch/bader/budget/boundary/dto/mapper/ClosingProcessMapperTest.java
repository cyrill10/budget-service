package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ClosingProcessBoundaryDto;
import ch.bader.budget.domain.ClosingProcess;
import ch.bader.budget.type.ClosingProcessStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ClosingProcessMapperTest {

    @Inject
    ClosingProcessBoundaryDtoMapperImpl sut;

    @Test
    void shouldMapClosingProcessToDto() {
        //given
        ClosingProcess domain = ClosingProcess.builder()
                                              .id("id")
                                              .yearMonth(YearMonth.of(2022, 1))
                                              .manualEntryStatus(ClosingProcessStatus.NEW)
                                              .uploadStatus(ClosingProcessStatus.DONE)
                                              .transferStatus(ClosingProcessStatus.STARTED)
                                              .build();

        //when
        ClosingProcessBoundaryDto dto = sut.mapToDto(domain);

        //then
        assertNotNull(dto);
        assertEquals("id", dto.getId());
        assertEquals(2022, dto.getYear());
        assertEquals(0, dto.getMonth());
        assertEquals(ClosingProcessStatus.NEW.getValue(), dto.getManualEntryStatus().getValue());
        assertEquals(ClosingProcessStatus.DONE.getValue(), dto.getUploadStatus().getValue());
    }
}