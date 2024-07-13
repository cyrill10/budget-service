package ch.bader.budget.boundary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatisticsBoundaryDto {

    private String id;
    private String name;
    private ValueEnumBoundaryDto accountType;
    private BigDecimal balanceIngoing;
    private BigDecimal balanceOutgoing;
}
