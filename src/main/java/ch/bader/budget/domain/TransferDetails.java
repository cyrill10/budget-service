package ch.bader.budget.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferDetails implements Comparable<TransferDetails> {

    private String accountName;
    private BigDecimal transferAmount;


    @Override
    public int compareTo(TransferDetails o) {
        return accountName.compareTo(o.getAccountName());
    }
}
