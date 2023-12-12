package ch.bader.budget.domain;

import ch.bader.budget.type.CardType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
@Builder
public class ScannedTransaction implements Comparable<ScannedTransaction> {
    private String id;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private Boolean transactionCreated;
    private CardType cardType;
    private YearMonth yearMonth;

    public ScannedTransaction createTransaction() {
        setTransactionCreated(true);
        return this;
    }

    @Override
    public int compareTo(ScannedTransaction o) {
        if (date.equals(o.date)) {
            return description.compareTo(o.description);
        }
        return date.compareTo(o.date);
    }
}
