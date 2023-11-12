package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.type.CardType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface CardTypeBoundaryDtoMapper {

    default String mapToString(CardType domain) {
        return domain.name();
    }

    default CardType mapToDomain(String name) {
        return CardType.valueOf(name);
    }
}