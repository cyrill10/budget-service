package ch.bader.budget.boundary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionBoundaryDto {
    private String id;
    private VirtualAccountBoundaryDto creditedAccount;
    private VirtualAccountBoundaryDto debitedAccount;
    private ZonedDateTime date;
    private String description;
    private ValueEnumBoundaryDto paymentStatus;
    private ValueEnumBoundaryDto indication;
    private ValueEnumBoundaryDto paymentType;
    private BigDecimal budgetedAmount;
    private BigDecimal effectiveAmount;
    private LocalDateTime creationDate;

}
