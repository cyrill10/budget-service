package ch.bader.budget.boundary.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VirtualAccountBoundaryDto {

    private String id;
    private String name;
    private BigDecimal balance;
    private Boolean isDeleted;
    private RealAccountBoundaryDto underlyingAccount;
}
