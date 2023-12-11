package ch.bader.budget.core.balance;

import ch.bader.budget.adapter.repository.YearMonthBalanceRepository;
import ch.bader.budget.boundary.time.MonthGenerator;
import ch.bader.budget.core.service.TransactionService;
import ch.bader.budget.core.service.VirtualAccountService;
import ch.bader.budget.domain.Balance;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.YearMonthBalance;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@ApplicationScoped
public class YearMonthBalanceCalculator {

    @Inject
    TransactionService transactionService;

    @Inject
    YearMonthBalanceRepository yearMonthBalanceRepository;

    @Inject
    VirtualAccountService virtualAccountService;

    @Inject
    MonthGenerator monthGenerator;

    @Inject
    AccountBalanceService accountBalanceService;

    @Scheduled(cron = "0 0 1 * * ?")
    void recalculateYearMonthBalances() {
        final LocalDate recalcDate = LocalDate.now().minusMonths(2).withDayOfMonth(1);

        recalculateYearMonthBalance(recalcDate);

    }

    public void recalculateYearMonthBalance(final LocalDate recalcDate) {
        yearMonthBalanceRepository.deleteAllYearMonthBalances();
        final List<Transaction> allTransactions = transactionService.getAllTransactionsForMonth(YearMonth.from(
            recalcDate));

        final List<VirtualAccount> allVirtualAccounts = virtualAccountService.getAllVirtualAccounts();

        final List<LocalDate> allMonthsToCalculate = monthGenerator.getAllMonths(recalcDate);

        allMonthsToCalculate
            .stream()
            .map(YearMonth::from)
            .forEach(yearMonth -> recalculateAndSaveMonth(yearMonth, allVirtualAccounts, allTransactions));
    }

    private void recalculateAndSaveMonth(final YearMonth yearMonth,
                                         final List<VirtualAccount> allVirtualAccounts,
                                         final List<Transaction> transactions) {
        final List<YearMonthBalance> yearMonthBalances = allVirtualAccounts
            .stream()
            .map(virtualAccount -> recalculateSingleAccount(yearMonth, virtualAccount, transactions))
            .toList();

        yearMonthBalanceRepository.saveAll(yearMonthBalances);
    }

    private YearMonthBalance recalculateSingleAccount(final YearMonth yearMonth,
                                                      final VirtualAccount virtualAccount,
                                                      final List<Transaction> list) {

        final Balance balanceAtYearMonth = accountBalanceService.getBalanceAtYearMonth(virtualAccount, yearMonth, list);

        return YearMonthBalance
            .builder()
            .balance(balanceAtYearMonth.getEffective())
            .yearMonth(yearMonth)
            .virtualAccountId(virtualAccount.getId())
            .build();
    }
}
