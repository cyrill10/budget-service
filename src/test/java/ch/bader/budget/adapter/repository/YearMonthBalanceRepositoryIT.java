package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.repository.mongo.YearMonthBalanceRepositoryImpl;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.YearMonthBalance;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class YearMonthBalanceRepositoryIT {

    @Inject
    YearMonthBalanceRepositoryImpl sut;

    @Nested
    class GetLatestYearMonthBalanceForVirtualAccount {

        @Test
        void shouldReturnLatestBalance() {
            //arrange
            sut.deleteAllYearMonthBalances();

            final YearMonthBalance yearMonthBalance1 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 5))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance2 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 3))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance3 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 1))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance4 = YearMonthBalance
                .builder()
                .virtualAccountId("account2")
                .yearMonth(YearMonth.of(2022, 1))
                .id(new ObjectId().toString())
                .build();
            sut.saveAll(List.of(yearMonthBalance1, yearMonthBalance2, yearMonthBalance3, yearMonthBalance4));

            final VirtualAccount account = VirtualAccount.builder().id("account1").build();
            //act
            final Optional<YearMonthBalance> result = sut.getLatestYearMonthBalanceForVirtualAccount(account,
                YearMonth.of(2022, 3));

            assertTrue(result.isPresent());
            assertEquals(yearMonthBalance2.getId(), result.get().getId());
        }

        @Test
        void shouldReturnEmptyIfNoneIsPresent() {
            //arrange
            sut.deleteAllYearMonthBalances();

            final YearMonthBalance yearMonthBalance1 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 5))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance2 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 3))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance3 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 1))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance4 = YearMonthBalance
                .builder()
                .virtualAccountId("account2")
                .yearMonth(YearMonth.of(2022, 1))
                .id(new ObjectId().toString())
                .build();
            sut.saveAll(List.of(yearMonthBalance1, yearMonthBalance2, yearMonthBalance3, yearMonthBalance4));

            final VirtualAccount account = VirtualAccount.builder().id("account1").build();
            //act
            final Optional<YearMonthBalance> result = sut.getLatestYearMonthBalanceForVirtualAccount(account,
                YearMonth.of(2021, 11));

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetLatestYearMonthBalanceForVirtualAccounts {

        @Test
        void shouldReturnBalances() {
            //arrange
            sut.deleteAllYearMonthBalances();

            final YearMonthBalance yearMonthBalance1 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 5))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance2 = YearMonthBalance
                .builder()
                .virtualAccountId("account2")
                .yearMonth(YearMonth.of(2022, 5))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance3 = YearMonthBalance
                .builder()
                .virtualAccountId("account3")
                .yearMonth(YearMonth.of(2022, 5))
                .id(new ObjectId().toString())
                .build();

            sut.saveAll(List.of(yearMonthBalance1, yearMonthBalance2, yearMonthBalance3));

            final VirtualAccount account1 = VirtualAccount.builder().id("account1").build();
            final VirtualAccount account2 = VirtualAccount.builder().id("account2").build();

            //act
            final List<YearMonthBalance> result = sut.getLatestYearMonthBalanceForVirtualAccounts(List.of(account1,
                account2), YearMonth.of(2022, 5));

            assertEquals(2, result.size());
            result.forEach(r -> assertEquals(YearMonth.of(2022, 5), r.getYearMonth()));
        }

        @Test
        void shouldReturnEmptyListIfDatesDontMatch() {
            //arrange
            sut.deleteAllYearMonthBalances();

            final YearMonthBalance yearMonthBalance1 = YearMonthBalance
                .builder()
                .virtualAccountId("account1")
                .yearMonth(YearMonth.of(2022, 5))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance2 = YearMonthBalance
                .builder()
                .virtualAccountId("account2")
                .yearMonth(YearMonth.of(2022, 5))
                .id(new ObjectId().toString())
                .build();
            final YearMonthBalance yearMonthBalance3 = YearMonthBalance
                .builder()
                .virtualAccountId("account3")
                .yearMonth(YearMonth.of(2022, 6))
                .id(new ObjectId().toString())
                .build();

            sut.saveAll(List.of(yearMonthBalance1, yearMonthBalance2, yearMonthBalance3));

            final VirtualAccount account1 = VirtualAccount.builder().id("account1").build();
            final VirtualAccount account2 = VirtualAccount.builder().id("account2").build();
            final VirtualAccount account3 = VirtualAccount.builder().id("account3").build();

            //act
            final List<YearMonthBalance> result = sut.getLatestYearMonthBalanceForVirtualAccounts(List.of(account1,
                account2,
                account3), YearMonth.of(2022, 6));

            assertEquals(0, result.size());
        }
    }

}