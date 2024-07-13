package ch.bader.budget.boundary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatisticsRequestBoundaryDto {

    private String realAccountId;
    private String virtualAccountId;
    private Integer fromYear;
    private Integer fromMonth;
    private Integer toYear;
    private Integer toMonth;
}
