package ch.bader.budget.adapter.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "yearMonthBalance")
public class YearMonthBalanceAdapterDbo {

    private ObjectId id;
    private String yearMonth;
    private ObjectId virtualAccountId;
    private String balance;

}
