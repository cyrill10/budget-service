package ch.bader.budget.adapter.repository;

import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface TransactionRepository {
    void saveTransactions(List<Transaction> transactionList);

    List<Transaction> findAllByDateBetween(LocalDate fromExclusive, LocalDate toExclusive);

    List<Transaction> findAllByDateBetweenAndVirtualAccountId(LocalDate fromExclusive,
                                                              LocalDate toExclusive,
                                                              List<String> accountIds);

    Transaction addTransaction(Transaction transaction);

    Transaction updateTransaction(Transaction transaction);

    void deleteTransaction(String transactionId);

    Transaction getTransactionById(String id);

    List<Transaction> getAllTransactionsForMonth(YearMonth month);

    List<Transaction> getAllTransactionsForMonthAndVirtualAccounts(YearMonth month,
                                                                   List<VirtualAccount> virtualAccounts);

    List<Transaction> getAllTransactionsForVirtualAccountsUntilDate(List<VirtualAccount> virtualAccounts,
                                                                    LocalDate unitlExclusive);

    List<Transaction> getAllTransactionsForVirtualAccountUntilDate(String virtualAccountId, LocalDate unitlExclusive);

    List<Transaction> getAllTransactionsUntil(YearMonth month);
}
