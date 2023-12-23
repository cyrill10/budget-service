package ch.bader.budget.boundary.dto.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Mapper
public interface LocalDateMapper {

    default LocalDateTime fromDomain(final LocalDate domain) {
        if (domain == null) {
            return null;
        }
        return LocalDateTime.of(domain, LocalTime.NOON);
    }

    default LocalDate toDomain(final LocalDateTime dto) {
        if (dto == null) {
            return null;
        }
        return LocalDate.from(dto);
    }
}
