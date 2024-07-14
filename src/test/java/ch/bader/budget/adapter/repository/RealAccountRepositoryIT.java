package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.RealAccountAdapterDbo;
import ch.bader.budget.adapter.entity.ValueEnumAdapterDbo;
import ch.bader.budget.adapter.repository.mongo.RealAccountRepositoryImpl;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.type.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class RealAccountRepositoryIT {

    @Inject
    RealAccountRepositoryImpl sut;

    @Test
    void getAllByAccountType() {

        //-- arrange
        final RealAccountAdapterDbo checking = RealAccountAdapterDbo
            .builder()
            .name("checking")
            .accountType(ValueEnumAdapterDbo.builder().value(1).name("Checking").build())
            .build();
        final RealAccountAdapterDbo saving = RealAccountAdapterDbo
            .builder()
            .name("saving")
            .accountType(ValueEnumAdapterDbo.builder().value(2).name("Saving").build())
            .build();
        final RealAccountAdapterDbo credits = RealAccountAdapterDbo
            .builder()
            .name("credit")
            .accountType(ValueEnumAdapterDbo.builder().value(3).name("Credit").build())
            .build();

        sut.persist(List.of(checking, saving, credits));

        //-- act
        List<RealAccount> results = sut.getAllByAccountType(List.of(AccountType.CHECKING, AccountType.SAVING));

        //-- assert
        assertEquals(2, results.size());
    }
}