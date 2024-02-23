package ch.bader.budget.core.process.closing;

import ch.bader.budget.domain.ScannedTransaction;
import ch.bader.budget.type.CardType;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.csv.CSVFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class CsvParsingService {

    public List<ScannedTransaction> parseCsvFile(final BufferedReader reader,
                                                 final YearMonth yearMonth) throws IOException {

        final CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
        
        return StreamSupport
            .stream(csvFormat.parse(reader).spliterator(), false)
            .map(record -> ScannedTransaction
                .builder()
                .date(LocalDate.parse(record.get(0), DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .description(record.get(1))
                .cardType(getCardType(record.get(2)))
                .amount(BigDecimal.valueOf(Double.parseDouble(record.get(4))))
                .yearMonth(yearMonth)
                .transactionCreated(false)
                .build())
            .filter(stb -> !stb.getDescription().contains("IHRE ZAHLUNG – BESTEN DANK"))
            .sorted()
            .toList();
    }

    private CardType getCardType(final String cardNumber) {
        return cardNumber.endsWith("9709") ? CardType.AMEX : CardType.VISA;
    }

}
