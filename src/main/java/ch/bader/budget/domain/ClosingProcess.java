package ch.bader.budget.domain;

import ch.bader.budget.type.ClosingProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClosingProcess implements Comparable<ClosingProcess> {

    private String id;
    YearMonth yearMonth;
    private ClosingProcessStatus uploadStatus;
    private ClosingProcessStatus manualEntryStatus;
    private ClosingProcessStatus transferStatus;

    @Override
    public int compareTo(ClosingProcess o) {
        return yearMonth.compareTo(o.yearMonth);
    }
}
