package ch.bader.budget.adapter.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@MongoEntity(collection = "closingProcess")
public class ClosingProcessAdapterDbo {

    private ObjectId id;
    private String yearMonth;
    private ValueEnumAdapterDbo uploadStatus;
    private ValueEnumAdapterDbo manualEntryStatus;
    private ValueEnumAdapterDbo transferStatus;

}
