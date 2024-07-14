package ch.bader.budget.domain.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightElement implements Comparable<InsightElement> {

    String name;
    BigDecimal amount;

    @Override
    public int compareTo(final InsightElement o) {
        if (amount.equals(o.amount)) {
            return name.compareTo(o.name);
        }
        return o.amount.compareTo(amount);
    }
}
