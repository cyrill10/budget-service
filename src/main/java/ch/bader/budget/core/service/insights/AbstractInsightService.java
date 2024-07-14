package ch.bader.budget.core.service.insights;

import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.core.service.TransactionService;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.statistics.InsightElement;
import ch.bader.budget.domain.statistics.InsightsRequest;
import ch.bader.budget.type.AccountType;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractInsightService {

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    @Inject
    RealAccountRepository realAccountRepository;

    @Inject
    TransactionService transactionService;

    protected List<Transaction> getInsightTransactions(final InsightsRequest insightsRequest,
                                                       final List<VirtualAccount> spendingVirtualAccount) {
        return getRelevantMonths(insightsRequest)
            .stream()
            .map(yearMonth -> transactionService.getAllTransactionsForMonthAndVirtualAccounts(yearMonth,
                spendingVirtualAccount))
            .flatMap(Collection::stream)
            .toList();
    }

    protected List<InsightElement> transformTransactionMap(final Map<VirtualAccount, List<Transaction>> spendingTransactionsBySpendingReason) {
        return spendingTransactionsBySpendingReason
            .entrySet()
            .stream()
            .map(entry -> InsightElement
                .builder()
                .name(entry.getKey().getName())
                .amount(entry
                    .getValue()
                    .stream()
                    .map(Transaction::getEffectiveAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build())
            .sorted()
            .toList();
    }

    protected List<VirtualAccount> getInsightVirtualAccounts(final List<String> accountIds,
                                                             final List<AccountType> accountTypes) {
        final List<RealAccount> relevantRealAccounts = realAccountRepository.getAllByAccountType(accountTypes);
        return virtualAccountRepository
            .getAccountsByRealAccounts(relevantRealAccounts)
            .stream()
            .filter(virtualAccount -> CollectionUtils.isEmpty(accountIds) || accountIds.contains(virtualAccount.getId()))
            .toList();
    }

    protected List<YearMonth> getRelevantMonths(final InsightsRequest insightsRequest) {
        if (CollectionUtils.isEmpty(insightsRequest.getMonths()) && CollectionUtils.isEmpty(insightsRequest.getYears())) {
            return List.of(YearMonth.now());
        }
        final List<Month> months = CollectionUtils.isEmpty(insightsRequest.getMonths()) ? List.of(Month.values()) : insightsRequest
            .getMonths()
            .stream()
            .map(Month::of)
            .toList();

        return insightsRequest
            .getYears()
            .stream()
            .map(year -> months.stream().map(month -> YearMonth.of(year, month)).toList())
            .flatMap(Collection::stream)
            .toList();
    }
}
