package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.adapter.repository.TransactionRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.core.balance.TransactionListElementBuilderService;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.TransactionListElement;
import ch.bader.budget.domain.VirtualAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TransactionService {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    @Inject
    RealAccountRepository realAccountRepository;

    @Inject
    TransactionListElementBuilderService transactionListElementBuilderService;

    public Transaction addTransaction(final Transaction transaction) {
        if (transaction.getCreationDate() == null) {
            transaction.setCreationDate(LocalDateTime.now());
        }
        return transactionRepository.addTransaction(transaction);
    }

    public Transaction updateTransaction(final Transaction transaction) {
        return transactionRepository.updateTransaction(transaction);
    }

    public void deleteTransaction(final String transactionId) {
        transactionRepository.deleteTransaction(transactionId);

    }

    public void duplicateTransaction(final Transaction transaction) {
        LocalDate startDate = transaction.getDate();
        final LocalDate endDate = transaction.getDate().plusYears(1).withDayOfMonth(1).withMonth(1);
        final List<Transaction> newTransactions = new ArrayList<>();

        while (startDate.isBefore(endDate)) {
            startDate = startDate.plusMonths(1);
            newTransactions.add(transaction.createDuplicate(startDate));
        }
        transactionRepository.saveTransactions(newTransactions);
    }

    public List<Transaction> getAllTransactionsForMonth(final YearMonth month) {
        return transactionRepository.getAllTransactionsForMonth(month);
    }

    public List<Transaction> getAllTransactionsForMonthAndVirtualAccounts(final YearMonth month,
                                                                          final List<VirtualAccount> virtualAccounts) {
        return transactionRepository.getAllTransactionsForMonthAndVirtualAccounts(month, virtualAccounts);
    }

    public List<TransactionListElement> getAllTransactionsForMonthAndVirtualAccount(final LocalDate date,
                                                                                    final String accountId) {
        final VirtualAccount virtualAccount = virtualAccountRepository.getAccountById(accountId);

        final List<Transaction> allTransactionsForAccount = transactionRepository.getAllTransactionsForVirtualAccountUntilDate(
            accountId,
            date.plusMonths(1));

        return transactionListElementBuilderService.getTransactionListElementsForMonth(allTransactionsForAccount,
            virtualAccount,
            YearMonth.from(date));
    }

    public List<TransactionListElement> getAllTransactionsForMonthAndRealAccount(final LocalDate date,
                                                                                 final String accountId) {

        final RealAccount realAccount = realAccountRepository.getAccountById(accountId);

        final List<VirtualAccount> virtualAccounts = virtualAccountRepository.getAccountsByRealAccount(realAccount);

        final List<Transaction> allTransactions = transactionRepository.getAllTransactionsForVirtualAccountsUntilDate(
            virtualAccounts,
            date.plusMonths(1).withDayOfMonth(1));

        return transactionListElementBuilderService.getTransactionListElementsForMonth(allTransactions,
            realAccount,
            virtualAccounts,
            YearMonth.from(date));
    }

    public Transaction getTransactionById(final String id) {
        return transactionRepository.getTransactionById(id);
    }
}
