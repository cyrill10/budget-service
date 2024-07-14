package ch.bader.budget.domain.statistics;

import ch.bader.budget.type.InsightType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightsRequest {

    private List<Integer> months;
    private List<Integer> years;
    private List<String> accountIds;
    private InsightType insightType;
}
