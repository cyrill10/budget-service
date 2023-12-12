package ch.bader.budget.boundary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClosingProcessBoundaryDto {

    private String id;
    private Integer year;
    private Integer month;
    private ValueEnumBoundaryDto uploadStatus;
    private ValueEnumBoundaryDto manualEntryStatus;
    private ValueEnumBoundaryDto transferStatus;
}
