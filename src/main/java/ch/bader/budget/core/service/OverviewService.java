package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.TransactionRepository;
import ch.bader.budget.core.balance.AccountBalanceService;
import ch.bader.budget.domain.Balance;
import ch.bader.budget.domain.OverviewElement;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.YearMonth;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class OverviewService {

    @Inject
    VirtualAccountService virtualAccountService;

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    AccountBalanceService accountBalanceService;

    public List<OverviewElement> getAllTransactions(final YearMonth yearMonth) {
        final Map<RealAccount, List<VirtualAccount>> accountMap = virtualAccountService.getAccountMap();
        final List<Transaction> allTransactions = transactionRepository.getAllTransactionsUntil(yearMonth.withMonth(12));

        return calculateOverview(accountMap, allTransactions, yearMonth);
    }

    private List<OverviewElement> calculateOverview(final Map<RealAccount, List<VirtualAccount>> accountMap,
                                                    final List<Transaction> transactions,
                                                    final YearMonth yearMonth) {
        return accountMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().getAccountType().isOverviewAccount())
            .map(account -> getOverviewElementListFromRealAccount(account.getKey(),
                account.getValue(),
                transactions,
                yearMonth))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());


    }

    private List<OverviewElement> getOverviewElementListFromRealAccount(final RealAccount realAccount,
                                                                        final List<VirtualAccount> virtualAccounts,
                                                                        final List<Transaction> transactions,
                                                                        final YearMonth yearMonth) {

        final List<OverviewElement> overviewElements = virtualAccounts
            .stream()
            .map(virtualAccount -> virtualAccountToOverviewElement(virtualAccount, transactions, yearMonth))
            .collect(Collectors.toCollection(LinkedList::new));

        overviewElements.add(0, realAccountToOverviewElement(realAccount, virtualAccounts, transactions, yearMonth));

        return overviewElements;
    }

    private OverviewElement virtualAccountToOverviewElement(final VirtualAccount virtualAccount,
                                                            final List<Transaction> transactions,
                                                            final YearMonth yearMonth) {

        final Balance balanceEndOfMonth = accountBalanceService.getBalanceAtYearMonth(virtualAccount,
            yearMonth,
            transactions);

        final Balance balanceEndOfYear;
        if (virtualAccount.isPrebudgetedAccount()) {
            balanceEndOfYear = new Balance(balanceEndOfMonth.getBudgeted().subtract(balanceEndOfMonth.getEffective()),
                null);
        } else {
            balanceEndOfYear = accountBalanceService.getBalanceAtYearMonth(virtualAccount,
                yearMonth.withMonth(12),
                transactions);
        }

        return getOverviewElement(balanceEndOfMonth,
            balanceEndOfYear,
            virtualAccount.getName(),
            virtualAccount.getId(),
            false);
    }

    private OverviewElement realAccountToOverviewElement(final RealAccount realAccount,
                                                         final List<VirtualAccount> virtualAccounts,
                                                         final List<Transaction> transactions,
                                                         final YearMonth yearMonth) {

        final Balance balanceEndOfMonth = accountBalanceService.getBalanceAtYearMonth(realAccount,
            virtualAccounts,
            yearMonth,
            transactions);

        final Balance balanceEndOfYear;
        if (realAccount.isPrebudgetedAccount()) {
            balanceEndOfYear = new Balance(balanceEndOfMonth.getBudgeted().subtract(balanceEndOfMonth.getEffective()),
                null);
        } else {
            balanceEndOfYear = accountBalanceService.getBalanceAtYearMonth(realAccount,
                virtualAccounts,
                yearMonth.withMonth(12),
                transactions);
        }

        return getOverviewElement(balanceEndOfMonth,
            balanceEndOfYear,
            realAccount.getName(),
            realAccount.getId(),
            true);
    }

    private OverviewElement getOverviewElement(final Balance balanceEndOfMonth,
                                               final Balance balanceEndOfYear,
                                               final String accountName,
                                               final String accountId,
                                               final boolean isRealAccount) {

        return OverviewElement
            .builder()
            .name(accountName)
            .id(accountId)
            .isRealAccount(isRealAccount)
            .balanceAfter(balanceEndOfMonth.getEffective())
            .budgetedBalanceAfter(balanceEndOfMonth.getBudgeted())
            .projection(balanceEndOfYear.getEffective())
            .budgetedProjection(balanceEndOfYear.getBudgeted())
            .build();
    }
}
