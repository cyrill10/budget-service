package ch.bader.budget.adapter.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "virtualAccount")
public class VirtualAccountAdapterDbo {

    @BsonId
    private String id;
    private String name;
    private BigDecimal balance;
    private Boolean isDeleted;
    private String underlyingAccountId;

}
