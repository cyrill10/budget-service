package ch.bader.budget.core.service;

import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.TransactionListElement;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TransactionService {
    public Transaction updateTransaction(Transaction transaction) {
        return null;
    }

    public void deleteTransaction(String transactionId) {

    }

    public void duplicateTransaction(Transaction transaction) {

    }

    public List<Transaction> getAllTransactions(LocalDate date) {
        return null;
    }

    public List<TransactionListElement> getAllTransactionsForMonthAndVirtualAccount(LocalDate date, String accountId) {
        return null;
    }

    public List<TransactionListElement> getAllTransactionsForMonthAndRealAccount(LocalDate date,
                                                                                 String accountId) {
        return null;
    }

    public Transaction getTransactionById(String id) {
        return null;
    }
}
