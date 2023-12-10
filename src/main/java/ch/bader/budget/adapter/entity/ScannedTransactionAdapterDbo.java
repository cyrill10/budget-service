package ch.bader.budget.adapter.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "scannedTransaction")
public class ScannedTransactionAdapterDbo {

    private ObjectId id;
    private String description;
    private String date;
    private BigDecimal amount;
    private Boolean transactionCreated;
    private String cardType;
    private String yearMonth;
}
