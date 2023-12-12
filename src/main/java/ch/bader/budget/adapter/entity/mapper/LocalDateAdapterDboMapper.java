package ch.bader.budget.adapter.entity.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDate;

@Mapper
public interface LocalDateAdapterDboMapper {

    default String toDbo(final LocalDate localDate) {
        return localDate.toString();
    }

    default LocalDate toDomain(final String s) {
        if (s == null) {
            return null;
        }
        return LocalDate.parse(s);
    }
}
