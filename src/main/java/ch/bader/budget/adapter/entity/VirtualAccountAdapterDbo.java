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
@MongoEntity(collection = "virtualAccount")
public class VirtualAccountAdapterDbo {

    private ObjectId id;
    private String name;
    private String balance;
    private Boolean isDeleted;
    private String underlyingAccountId;

}
