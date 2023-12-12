package ch.bader.budget.boundary.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScannedTransactionBoundaryDto {
    private String id;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private Boolean transactionCreated;
    private String cardType;
}
