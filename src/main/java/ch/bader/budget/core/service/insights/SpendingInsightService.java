package ch.bader.budget.core.service.insights;

import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.statistics.Insights;
import ch.bader.budget.domain.statistics.InsightsRequest;
import ch.bader.budget.type.AccountType;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@ApplicationScoped
public class SpendingInsightService extends AbstractInsightService {

    public Insights getSpendingsInsights(final InsightsRequest insightsRequest) {
        final List<VirtualAccount> spendingVirtualAccounts = getInsightVirtualAccounts(insightsRequest.getAccountIds(),
            List.of(AccountType.ALIEN, AccountType.PREBUDGETED));
        final List<Transaction> transactions = getInsightTransactions(insightsRequest, spendingVirtualAccounts);

        final Map<VirtualAccount, List<Transaction>> spendingTransactionsBySpendingReason = transactions
            .stream()
            .filter(transaction -> spendingVirtualAccounts.contains(transaction.getDebitedAccount()))
            .filter(transaction -> !spendingVirtualAccounts.contains(transaction.getCreditedAccount()))
            .collect(groupingBy(Transaction::getDebitedAccount));

        return Insights.builder().insights(transformTransactionMap(spendingTransactionsBySpendingReason)).build();
    }
}
