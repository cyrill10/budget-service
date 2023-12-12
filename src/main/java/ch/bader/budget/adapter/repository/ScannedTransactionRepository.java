package ch.bader.budget.adapter.repository;

import ch.bader.budget.domain.ScannedTransaction;

import java.time.YearMonth;
import java.util.List;

public interface ScannedTransactionRepository {
    List<ScannedTransaction> findAllById(List<String> ids);

    List<ScannedTransaction> findAllByYearMonth(YearMonth yearMonth);

    List<ScannedTransaction> saveAll(List<ScannedTransaction> scannedTransactions);

    void updateAll(List<ScannedTransaction> scannedTransactions);
}
