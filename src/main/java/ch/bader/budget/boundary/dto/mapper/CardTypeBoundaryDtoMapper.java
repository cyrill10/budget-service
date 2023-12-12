package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.type.CardType;
import org.mapstruct.Mapper;

@Mapper
public interface CardTypeBoundaryDtoMapper {

    default String mapToString(final CardType domain) {
        return domain.name();
    }

    default CardType mapToDomain(final String name) {
        return CardType.valueOf(name);
    }
}