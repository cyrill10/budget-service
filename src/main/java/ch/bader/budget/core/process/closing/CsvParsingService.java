package ch.bader.budget.core.process.closing;

import ch.bader.budget.domain.ScannedTransaction;
import ch.bader.budget.type.CardType;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class CsvParsingService {

    public List<ScannedTransaction> parseCsvFile(final BufferedReader reader,
                                                 final YearMonth yearMonth) throws IOException {


        final CSVFormat csvFormat = CSVFormat.DEFAULT.builder().build();
        final CSVParser parser = csvFormat.parse(reader);
        final var records = parser.getRecords();
        final CSVRecord headerRecords = records.get(0);
        final Map<HeaderEnum, Integer> headerMap = Stream
            .of(HeaderEnum.values())
            .collect(toMap(Function.identity(),
                headerEnum -> IntStream
                    .range(0, headerRecords.size())
                    .filter(headerIndex -> headerEnum.isHeader(headerRecords.get(headerIndex)))
                    .findFirst()
                    .orElseThrow()));

        return records
            .stream()
            .skip(1)
            .map(record -> ScannedTransaction
                .builder()
                .date(LocalDate.parse(record.get(headerMap.get(HeaderEnum.DATE)),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .description(record.get(headerMap.get(HeaderEnum.DESCRIPTION)))
                .cardType(getCardType(record.get(headerMap.get(HeaderEnum.CARD_NUMBER))))
                .amount(BigDecimal.valueOf(Double.parseDouble(record.get(headerMap.get(HeaderEnum.AMOUNT)))))
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

    @Getter
    private enum HeaderEnum {

        DATE("Transaktionsdatum", "Transaction date"),  //
        DESCRIPTION("Beschreibung", "Description"),  //
        CARD_NUMBER("Kartennummer", "Card number"), //
        AMOUNT("Betrag", "Amount");

        HeaderEnum(final String germanName, final String englishName) {
            this.germanName = germanName;
            this.englishName = englishName;
        }

        private final String germanName;
        private final String englishName;

        boolean isHeader(final String headerName) {
            return germanName.equals(headerName) || englishName.equals(headerName);
        }
    }
}
