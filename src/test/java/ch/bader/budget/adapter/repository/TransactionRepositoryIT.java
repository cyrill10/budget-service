package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.TransactionAdapterDbo;
import ch.bader.budget.domain.Transaction;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class TransactionRepositoryIT {

    @Inject
    TransactionRepository sut;

    @Test
    @TestTransaction
    void findAllByDateBetween() {
        //arrange
        final LocalDate july31st = LocalDate.of(2022, 7, 31);
        final TransactionAdapterDbo lastDayOfMonthBefore = TransactionAdapterDbo.builder().date(july31st).build();

        final LocalDate aug1st = LocalDate.of(2022, 8, 1);
        final TransactionAdapterDbo firstDayOfMonth = TransactionAdapterDbo.builder().date(aug1st).build();

        final LocalDate aug31st = LocalDate.of(2022, 8, 31);
        final TransactionAdapterDbo lastDayOfMonth = TransactionAdapterDbo.builder().date(aug31st).build();

        final LocalDate sep1st = LocalDate.of(2022, 9, 1);
        final TransactionAdapterDbo firstDayOfMonthAfter = TransactionAdapterDbo.builder().date(sep1st).build();

        sut.persist(List.of(lastDayOfMonthBefore, firstDayOfMonth, lastDayOfMonth, firstDayOfMonthAfter));

        final List<Transaction> transactions = sut.findAllByDateBetween(july31st, sep1st);
        assertThat(transactions, hasSize(2));

        assertThat(transactions, contains(
            allOf(
                hasProperty(TransactionAdapterDbo.Fields.date, is(firstDayOfMonth.getDate()))
            ),
            allOf(
                hasProperty(TransactionAdapterDbo.Fields.date, is(lastDayOfMonth.getDate()))
            )
        ));
    }


    @Test
    @TestTransaction
    void findAllByDateBetweenAndVirtualAccountId() {
        final String accountIncluded = "accountIncluded";
        final String accountAlsoIncluded = "accountAlsoIncluded";

        final String accountExcluded = "accountExcluded";


        //arrange
        final LocalDate july31st = LocalDate.of(2022, 7, 31);
        final TransactionAdapterDbo lastDayOfMonthBeforeIncl = TransactionAdapterDbo
            .builder()
            .date(july31st)
            .creditedAccountId(accountIncluded)
            .debitedAccountId(accountAlsoIncluded)
            .build();

        final LocalDate aug1st = LocalDate.of(2022, 8, 1);
        final TransactionAdapterDbo firstDayOfMonthExcl = TransactionAdapterDbo
            .builder()
            .date(aug1st)
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountExcluded)
            .build();
        final TransactionAdapterDbo firstDayOfMonthIncl = TransactionAdapterDbo
            .builder()
            .date(aug1st)
            .debitedAccountId(accountIncluded)
            .creditedAccountId(accountExcluded)
            .build();
        final TransactionAdapterDbo firstDayOfMonthDoubleIncl = TransactionAdapterDbo
            .builder()
            .date(aug1st)
            .debitedAccountId(accountIncluded)
            .creditedAccountId(accountIncluded)
            .build();


        final LocalDate aug31st = LocalDate.of(2022, 8, 31);
        final TransactionAdapterDbo lastDayOfMonthExcl = TransactionAdapterDbo
            .builder()
            .date(aug31st)
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountExcluded)
            .build();

        final TransactionAdapterDbo lastDayOfMonthIncl = TransactionAdapterDbo
            .builder()
            .date(aug31st)
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountIncluded)
            .build();

        final TransactionAdapterDbo lastDayOfMonthAlsoIncl = TransactionAdapterDbo
            .builder()
            .date(aug31st)
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountAlsoIncluded)
            .build();

        final TransactionAdapterDbo lastDayOfMonthDoubleIncl = TransactionAdapterDbo
            .builder()
            .date(aug31st)
            .debitedAccountId(accountIncluded)
            .creditedAccountId(accountAlsoIncluded)
            .build();

        final LocalDate sep1st = LocalDate.of(2022, 9, 1);
        final TransactionAdapterDbo firstDayOfMonthAfterIncl = TransactionAdapterDbo
            .builder()
            .date(sep1st)
            .creditedAccountId(accountIncluded)
            .debitedAccountId(accountAlsoIncluded)
            .build();


        sut.persist(List.of(
            lastDayOfMonthBeforeIncl,
            firstDayOfMonthExcl,
            firstDayOfMonthIncl,
            firstDayOfMonthDoubleIncl,
            lastDayOfMonthExcl,
            lastDayOfMonthIncl,
            lastDayOfMonthAlsoIncl,
            lastDayOfMonthDoubleIncl,
            firstDayOfMonthAfterIncl));

        // act
        final List<Transaction> transactions = sut.findAllByDateBetweenAndVirtualAccountId(
            july31st,
            sep1st,
            List.of(accountIncluded, accountAlsoIncluded)
        );

        // assert
        assertThat(transactions,
            hasSize(5));
        assertThat(transactions, contains(
            allOf(
                hasProperty(TransactionAdapterDbo.Fields.date, is(firstDayOfMonthIncl.getDate())),
                hasProperty(TransactionAdapterDbo.Fields.debitedAccountId,
                    is(firstDayOfMonthIncl.getDebitedAccountId())),
                hasProperty(TransactionAdapterDbo.Fields.creditedAccountId,
                    is(firstDayOfMonthIncl.getCreditedAccountId()))
            ),
            allOf(
                hasProperty(TransactionAdapterDbo.Fields.date, is(firstDayOfMonthDoubleIncl.getDate())),
                hasProperty(TransactionAdapterDbo.Fields.debitedAccountId,
                    is(firstDayOfMonthDoubleIncl.getDebitedAccountId())),
                hasProperty(TransactionAdapterDbo.Fields.creditedAccountId,
                    is(firstDayOfMonthDoubleIncl.getCreditedAccountId()))
            ),
            allOf(
                hasProperty(TransactionAdapterDbo.Fields.date, is(lastDayOfMonthIncl.getDate())),
                hasProperty(TransactionAdapterDbo.Fields.debitedAccountId,
                    is(lastDayOfMonthIncl.getDebitedAccountId())),
                hasProperty(TransactionAdapterDbo.Fields.creditedAccountId,
                    is(lastDayOfMonthIncl.getCreditedAccountId()))
            ),
            allOf(
                hasProperty(TransactionAdapterDbo.Fields.date, is(lastDayOfMonthAlsoIncl.getDate())),
                hasProperty(TransactionAdapterDbo.Fields.debitedAccountId,
                    is(lastDayOfMonthAlsoIncl.getDebitedAccountId())),
                hasProperty(TransactionAdapterDbo.Fields.creditedAccountId,
                    is(lastDayOfMonthAlsoIncl.getCreditedAccountId()))
            ),
            allOf(
                hasProperty(TransactionAdapterDbo.Fields.date, is(lastDayOfMonthDoubleIncl.getDate())),
                hasProperty(TransactionAdapterDbo.Fields.debitedAccountId,
                    is(lastDayOfMonthDoubleIncl.getDebitedAccountId())),
                hasProperty(TransactionAdapterDbo.Fields.creditedAccountId,
                    is(lastDayOfMonthDoubleIncl.getCreditedAccountId()))
            )));
    }
}