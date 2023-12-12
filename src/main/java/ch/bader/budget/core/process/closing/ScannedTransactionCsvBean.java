package ch.bader.budget.core.process.closing;

import ch.bader.budget.domain.ClosingProcess;
import ch.bader.budget.domain.ScannedTransaction;
import ch.bader.budget.type.CardType;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScannedTransactionCsvBean {

    @CsvBindByName(column = "Transaction date", required = true)
    @CsvDate("dd.MM.yyyy")
    private LocalDate transactionDate;

    @CsvBindByName(column = "Description", required = true)
    private String description;

    @CsvBindByName(column = "Card number", required = true)
    private String cardNumber;

    @CsvBindByName(column = "Amount", required = true)
    @CsvNumber("#.##")
    private BigDecimal amount;

    @CsvBindByName(column = "Debit/Credit", required = true)
    private String type;

    public ScannedTransaction mapTopScannedTransaction(final ClosingProcess closingProcess) {
        return ScannedTransaction.builder()
                                 .date(transactionDate)
                                 .transactionCreated(false)
                                 .amount(amount)
                                 .cardType(getCardType())
                                 .description(description)
                                 .yearMonth(closingProcess.getYearMonth())
                                 .build();
    }

    public CardType getCardType() {
        return cardNumber.endsWith("9709") ? CardType.AMEX : CardType.VISA;
    }

}
