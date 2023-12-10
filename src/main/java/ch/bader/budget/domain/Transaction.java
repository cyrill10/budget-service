package ch.bader.budget.domain;

import ch.bader.budget.type.PaymentStatus;
import ch.bader.budget.type.PaymentType;
import ch.bader.budget.type.TransactionIndication;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Transaction implements Comparable<Transaction> {
    private String id;
    private VirtualAccount creditedAccount;
    private VirtualAccount debitedAccount;
    private LocalDate date;
    private String description;
    private PaymentStatus paymentStatus;
    private TransactionIndication indication;
    private PaymentType paymentType;
    private BigDecimal budgetedAmount;
    private BigDecimal effectiveAmount;
    private LocalDateTime creationDate;

    public Transaction createDuplicate(final LocalDate newDate) {
        return Transaction
            .builder()
            .budgetedAmount(budgetedAmount)
            .effectiveAmount(effectiveAmount)
            .creditedAccount(creditedAccount)
            .debitedAccount(debitedAccount)
            .description(description)
            .paymentStatus(paymentStatus)
            .indication(indication)
            .paymentType(paymentType)
            .date(newDate)
            .creationDate(LocalDateTime.now())
            .build();
    }

    @Override
    public int compareTo(final Transaction o) {
        return creationDate.compareTo(o.creationDate);
    }

    public boolean isForAccount(final VirtualAccount virtualAccount) {
        return creditedAccount.equals(virtualAccount) || debitedAccount.equals(virtualAccount);
    }

    public boolean isForAccount(final RealAccount realAccount) {
        return creditedAccount.getUnderlyingAccount().equals(realAccount) || debitedAccount
            .getUnderlyingAccount()
            .equals(realAccount);
    }
}
