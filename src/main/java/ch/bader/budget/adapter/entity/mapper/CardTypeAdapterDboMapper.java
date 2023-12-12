package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.type.CardType;
import org.mapstruct.Mapper;

@Mapper
public interface CardTypeAdapterDboMapper {

    default String mapToDbo(final CardType domain) {
        return domain.name();
    }

    default CardType mapToDomain(final String name) {
        return CardType.valueOf(name);
    }
}