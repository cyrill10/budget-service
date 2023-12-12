package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.TransactionAdapterDbo;
import ch.bader.budget.adapter.repository.mongo.TransactionRepositoryImpl;
import ch.bader.budget.domain.Transaction;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class TransactionRepositoryIT {

    public static final LocalDateTime NOW = LocalDateTime.now();
    @Inject
    TransactionRepositoryImpl sut;

    @Test
    @TestTransaction
    void findAllByDateBetween() {
        //arrange
        final LocalDate july31st = LocalDate.of(2022, 7, 31);
        final TransactionAdapterDbo lastDayOfMonthBefore = TransactionAdapterDbo
            .builder()
            .date(july31st.toString())
            .creationDate(NOW)
            .build();

        final LocalDate aug1st = LocalDate.of(2022, 8, 1);
        final TransactionAdapterDbo firstDayOfMonth = TransactionAdapterDbo
            .builder()
            .date(aug1st.toString())
            .creationDate(NOW)
            .build();

        final LocalDate aug31st = LocalDate.of(2022, 8, 31);
        final TransactionAdapterDbo lastDayOfMonth = TransactionAdapterDbo
            .builder()
            .date(aug31st.toString())
            .creationDate(NOW)
            .build();

        final LocalDate sep1st = LocalDate.of(2022, 9, 1);
        final TransactionAdapterDbo firstDayOfMonthAfter = TransactionAdapterDbo
            .builder()
            .date(sep1st.toString())
            .creationDate(NOW)
            .build();

        sut.persist(List.of(lastDayOfMonthBefore, firstDayOfMonth, lastDayOfMonth, firstDayOfMonthAfter));

        final List<Transaction> transactions = sut.findAllByDateBetween(july31st, sep1st);
        assertThat(transactions, hasSize(2));

        assertThat(transactions,
            contains(allOf(hasProperty(TransactionAdapterDbo.Fields.date,
                    is(LocalDate.parse(firstDayOfMonth.getDate())))),
                allOf(hasProperty(TransactionAdapterDbo.Fields.date, is(LocalDate.parse(lastDayOfMonth.getDate()))))));
    }


    @Test
    @TestTransaction
    void findAllByDateBetweenAndVirtualAccountId() {
        final ObjectId accountIncludedId = ObjectId.get();
        final String accountIncluded = accountIncludedId.toString();

        final ObjectId accountAlsoIncludedId = ObjectId.get();
        final String accountAlsoIncluded = accountAlsoIncludedId.toString();

        final ObjectId accountExcludedId = ObjectId.get();
        final String accountExcluded = accountExcludedId.toString();


        //arrange
        final LocalDate july31st = LocalDate.of(2022, 7, 31);
        final TransactionAdapterDbo lastDayOfMonthBeforeIncl = TransactionAdapterDbo
            .builder()
            .date(july31st.toString())
            .description("lastDayOfMonthBeforeIncl")
            .creditedAccountId(accountIncluded)
            .debitedAccountId(accountAlsoIncluded)
            .creationDate(NOW)
            .build();

        final LocalDate aug1st = LocalDate.of(2022, 8, 1);
        final TransactionAdapterDbo firstDayOfMonthExcl = TransactionAdapterDbo
            .builder()
            .date(aug1st.toString())
            .description("firstDayOfMonthExcl")
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountExcluded)
            .creationDate(NOW)
            .build();
        final TransactionAdapterDbo firstDayOfMonthIncl = TransactionAdapterDbo
            .builder()
            .date(aug1st.toString())
            .description("firstDayOfMonthIncl")
            .debitedAccountId(accountIncluded)
            .creditedAccountId(accountExcluded)
            .creationDate(NOW)
            .build();
        final TransactionAdapterDbo firstDayOfMonthDoubleIncl = TransactionAdapterDbo
            .builder()
            .date(aug1st.toString())
            .description("firstDayOfMonthDoubleIncl")
            .debitedAccountId(accountIncluded)
            .creditedAccountId(accountIncluded)
            .creationDate(NOW)
            .build();


        final LocalDate aug31st = LocalDate.of(2022, 8, 31);
        final TransactionAdapterDbo lastDayOfMonthExcl = TransactionAdapterDbo
            .builder()
            .date(aug31st.toString())
            .description("lastDayOfMonthExcl")
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountExcluded)
            .creationDate(NOW)
            .build();

        final TransactionAdapterDbo lastDayOfMonthIncl = TransactionAdapterDbo
            .builder()
            .date(aug31st.toString())
            .description("lastDayOfMonthIncl")
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountIncluded)
            .creationDate(NOW)
            .build();

        final TransactionAdapterDbo lastDayOfMonthAlsoIncl = TransactionAdapterDbo
            .builder()
            .date(aug31st.toString())
            .description("lastDayOfMonthAlsoIncl")
            .debitedAccountId(accountExcluded)
            .creditedAccountId(accountAlsoIncluded)
            .creationDate(NOW)
            .build();

        final TransactionAdapterDbo lastDayOfMonthDoubleIncl = TransactionAdapterDbo
            .builder()
            .date(aug31st.toString())
            .description("lastDayOfMonthDoubleIncl")
            .debitedAccountId(accountIncluded)
            .creditedAccountId(accountAlsoIncluded)
            .creationDate(NOW)
            .build();

        final LocalDate sep1st = LocalDate.of(2022, 9, 1);
        final TransactionAdapterDbo firstDayOfMonthAfterIncl = TransactionAdapterDbo
            .builder()
            .date(sep1st.toString())
            .description("firstDayOfMonthAfterIncl")
            .creditedAccountId(accountIncluded)
            .debitedAccountId(accountAlsoIncluded)
            .creationDate(NOW)
            .build();


        sut.persist(List.of(lastDayOfMonthBeforeIncl,
            firstDayOfMonthExcl,
            firstDayOfMonthIncl,
            firstDayOfMonthDoubleIncl,
            lastDayOfMonthExcl,
            lastDayOfMonthIncl,
            lastDayOfMonthAlsoIncl,
            lastDayOfMonthDoubleIncl,
            firstDayOfMonthAfterIncl));

        // act

        final List<Transaction> transactions = sut.findAllByDateBetweenAndVirtualAccountId(july31st,
            sep1st,
            List.of(accountIncluded, accountAlsoIncluded));
        // assert
        assertThat(transactions, hasSize(5));
        assertThat(transactions,
            contains(allOf(hasProperty(TransactionAdapterDbo.Fields.date,
                        is(LocalDate.parse(firstDayOfMonthIncl.getDate()))),
                    hasProperty(TransactionAdapterDbo.Fields.description, is(firstDayOfMonthIncl.getDescription()))),
                allOf(hasProperty(TransactionAdapterDbo.Fields.date,
                        is(LocalDate.parse(firstDayOfMonthDoubleIncl.getDate()))),
                    hasProperty(TransactionAdapterDbo.Fields.description,
                        is(firstDayOfMonthDoubleIncl.getDescription()))),
                allOf(hasProperty(TransactionAdapterDbo.Fields.date, is(LocalDate.parse(lastDayOfMonthIncl.getDate()))),
                    hasProperty(TransactionAdapterDbo.Fields.description, is(lastDayOfMonthIncl.getDescription()))),
                allOf(hasProperty(TransactionAdapterDbo.Fields.date,
                        is(LocalDate.parse(lastDayOfMonthAlsoIncl.getDate()))),
                    hasProperty(TransactionAdapterDbo.Fields.description, is(lastDayOfMonthAlsoIncl.getDescription()))),
                allOf(hasProperty(TransactionAdapterDbo.Fields.date,
                        is(LocalDate.parse(lastDayOfMonthDoubleIncl.getDate()))),
                    hasProperty(TransactionAdapterDbo.Fields.description,
                        is(lastDayOfMonthDoubleIncl.getDescription())))));
    }
}