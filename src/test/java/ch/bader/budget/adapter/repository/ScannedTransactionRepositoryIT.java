package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.ScannedTransactionAdapterDbo;
import ch.bader.budget.adapter.repository.mongo.ScannedTransactionRepositoryImpl;
import ch.bader.budget.domain.ScannedTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class ScannedTransactionRepositoryIT {

    @Inject
    ScannedTransactionRepositoryImpl sut;

    @Test
    void findAllByYearMonth() {

        //arrange
        final ScannedTransactionAdapterDbo findableDbo = ScannedTransactionAdapterDbo
            .builder()
            .yearMonth("2021-01")
            .description("findableDbo")
            .build();

        final ScannedTransactionAdapterDbo alsoFindableDbo = ScannedTransactionAdapterDbo
            .builder()
            .yearMonth("2021-01")
            .description("alsoFindableDbo")
            .build();

        final ScannedTransactionAdapterDbo notFindableDbo = ScannedTransactionAdapterDbo
            .builder()
            .yearMonth("2021-02")
            .description("notFindableDbo")
            .build();

        final ScannedTransactionAdapterDbo alsoNotFindableDbo = ScannedTransactionAdapterDbo
            .builder()
            .yearMonth("2022-01")
            .description("alsoNotFindableDbo")
            .build();

        sut.persist(List.of(findableDbo, notFindableDbo, alsoNotFindableDbo, alsoFindableDbo));

        //act
        final List<ScannedTransaction> results = sut.findAllByYearMonth(YearMonth.of(2021, 1));

        assertThat(results, hasSize(2));
        assertThat(results,
            contains(hasProperty("description", is("findableDbo")), hasProperty("description", is("alsoFindableDbo"))));
    }
}