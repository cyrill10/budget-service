package ch.bader.budget.boundary.dto;

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
public class InsightsRequestBoundaryDto {

    private List<Integer> months;
    private List<Integer> years;
    private List<String> accountIds;
    private InsightType insightType;
}
