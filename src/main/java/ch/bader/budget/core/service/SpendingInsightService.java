package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.statistics.InsightElement;
import ch.bader.budget.domain.statistics.Insights;
import ch.bader.budget.domain.statistics.InsightsRequest;
import ch.bader.budget.type.AccountType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@ApplicationScoped
public class SpendingInsightService {

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    @Inject
    RealAccountRepository realAccountRepository;

    @Inject
    TransactionService transactionService;

    public Insights getSpendingsInsights(final InsightsRequest insightsRequest, final List<YearMonth> relevantMonths) {
        final List<VirtualAccount> spendingVirtualAccount = getSpendingVirtualAccount(insightsRequest.getAccountIds());
        final List<Transaction> transactions = relevantMonths
            .stream()
            .map(yearMonth -> transactionService.getAllTransactionsForMonthAndVirtualAccounts(yearMonth,
                spendingVirtualAccount))
            .flatMap(Collection::stream)
            .toList();

        final Map<VirtualAccount, List<Transaction>> spendingTransactionsBySpendingReason = transactions
            .stream()
            .filter(transaction -> spendingVirtualAccount.contains(transaction.getDebitedAccount()))
            .filter(transaction -> !spendingVirtualAccount.contains(transaction.getCreditedAccount()))
            .collect(groupingBy(Transaction::getDebitedAccount));

        final List<InsightElement> insightElements = spendingTransactionsBySpendingReason
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
            .toList();
        return Insights.builder().insights(insightElements).build();
    }

    private List<VirtualAccount> getSpendingVirtualAccount(final List<String> accountIds) {
        final List<RealAccount> relevantRealAccounts = realAccountRepository.getAllByAccountType(List.of(AccountType.ALIEN));
        return virtualAccountRepository
            .getAccountsByRealAccounts(relevantRealAccounts)
            .stream()
            .filter(virtualAccount -> CollectionUtils.isEmpty(accountIds) || accountIds.contains(virtualAccount.getId()))
            .toList();
    }
}
