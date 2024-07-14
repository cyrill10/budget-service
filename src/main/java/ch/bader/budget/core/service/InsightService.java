package ch.bader.budget.core.service;

import ch.bader.budget.domain.statistics.Insights;
import ch.bader.budget.domain.statistics.InsightsRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Month;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class InsightService {

    @Inject
    IncomeInsightService incomeInsightService;

    @Inject
    SpendingInsightService spendingInsightService;

    public Insights getInsights(final InsightsRequest insightsRequest) {
        final List<YearMonth> relevantMonths = getRelevantMonths(insightsRequest);
        return switch (insightsRequest.getInsightType()) {
            case INCOME -> incomeInsightService.getIncomeInsights(insightsRequest, relevantMonths);
            case SPENDING -> spendingInsightService.getSpendingsInsights(insightsRequest, relevantMonths);
            case SAVING -> null;
        };
    }

    private List<YearMonth> getRelevantMonths(final InsightsRequest insightsRequest) {
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
