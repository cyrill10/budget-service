package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.ScannedTransactionAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.ScannedTransactionAdapterDboMapper;
import ch.bader.budget.domain.ScannedTransaction;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.YearMonth;
import java.util.List;

@ApplicationScoped
public class ScannedTransactionRepository implements PanacheMongoRepositoryBase<ScannedTransactionAdapterDbo, String> {

    @Inject
    ScannedTransactionAdapterDboMapper scannedTransactionAdapterDboMapper;


    public List<ScannedTransaction> findAllByYearMonth(final YearMonth yearMonth) {
        return list("yearMonth", yearMonth.toString())
            .stream()
            .map(scannedTransactionAdapterDboMapper::mapToDomain)
            .toList();
    }

}
