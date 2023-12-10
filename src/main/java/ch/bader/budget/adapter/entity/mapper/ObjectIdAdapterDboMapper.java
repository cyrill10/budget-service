package ch.bader.budget.adapter.entity.mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;

@Mapper
public interface ObjectIdAdapterDboMapper {

    default ObjectId toDbo(final String id) {
        if (id == null) {
            return null;
        }
        return new ObjectId(id);
    }

    default String toString(final ObjectId id) {
        return id.toString();
    }
}
