package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.RealAccountBoundaryDto;
import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.type.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class RealAccountMapperTest {

    @Inject
    RealAccountBoundaryDtoMapperImpl sut;

    @Test
    void shouldMapRealAccountToDto() {
        //given
        RealAccount domain = RealAccount.builder()
                                        .id("id")
                                        .name("realAccountName")
                                        .accountType(AccountType.CHECKING)
                                        .build();

        //when
        RealAccountBoundaryDto account = sut.mapToDto(domain);

        //then
        assertNotNull(account);
        assertEquals(domain.getName(), account.getName());
        assertEquals(domain.getId(), account.getId());
        assertEquals(domain.getAccountType().getValue(),
            account.getAccountType().getValue());
    }

    @Test
    void shouldMapDtoToRealAccount() {
        //given
        RealAccountBoundaryDto dto = RealAccountBoundaryDto.builder()
                                                           .id("id")
                                                           .name("realAccountName")
                                                           .accountType(ValueEnumBoundaryDto.builder()
                                                                                            .value(AccountType.CHECKING.getValue())
                                                                                            .name(AccountType.CHECKING.getName())
                                                                                            .build())
                                                           .build();

        //when
        RealAccount account = sut.mapToDomain(dto);

        //then
        assertNotNull(account);
        assertEquals(dto.getName(), account.getName());
        assertEquals(dto.getId(), account.getId());
        assertEquals(dto.getAccountType().getValue(), account.getAccountType().getValue());
    }
}