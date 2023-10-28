package ch.bader.budget.boundary.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ScannedTransactionBoundaryDto {
    private String id;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private Boolean transactionCreated;
    private String cardType;
}
