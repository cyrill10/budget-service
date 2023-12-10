package ch.bader.budget.adapter.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@MongoEntity(collection = "transaction")
public class TransactionAdapterDbo {

    private ObjectId id;
    private String creditedAccountId;
    private String debitedAccountId;
    private String date;
    private String description;
    private ValueEnumAdapterDbo paymentStatus;
    private ValueEnumAdapterDbo indication;
    private ValueEnumAdapterDbo paymentType;
    private String budgetedAmount;
    private String effectiveAmount;
    private LocalDateTime creationDate;
}
