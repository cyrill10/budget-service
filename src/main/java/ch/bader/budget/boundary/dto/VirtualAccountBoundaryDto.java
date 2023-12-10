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
public class VirtualAccountBoundaryDto {

    private String id;
    private String name;
    private BigDecimal balance;
    private Boolean isDeleted;
    private RealAccountBoundaryDto underlyingAccount;
}
