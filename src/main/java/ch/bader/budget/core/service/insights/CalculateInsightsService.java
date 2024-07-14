package ch.bader.budget.core.service.insights;

import ch.bader.budget.domain.statistics.Insights;
import ch.bader.budget.domain.statistics.InsightsRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CalculateInsightsService {

    @Inject
    IncomeInsightService incomeInsightService;

    @Inject
    SpendingInsightService spendingInsightService;

    @Inject
    SavingsInsightService savingsInsightService;

    public Insights getInsights(final InsightsRequest insightsRequest) {
        return switch (insightsRequest.getInsightType()) {
            case INCOME -> incomeInsightService.getIncomeInsights(insightsRequest);
            case SPENDINGS -> spendingInsightService.getSpendingsInsights(insightsRequest);
            case SAVINGS -> savingsInsightService.getSavingsInsight(insightsRequest);
        };
    }

}
