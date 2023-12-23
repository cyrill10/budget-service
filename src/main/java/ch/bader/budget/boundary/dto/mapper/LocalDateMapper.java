package ch.bader.budget.boundary.dto.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper
public interface LocalDateMapper {

    default ZonedDateTime fromDomain(final LocalDate domain) {
        if (domain == null) {
            return null;
        }
        return ZonedDateTime.of(domain, LocalTime.NOON, ZoneId.of("Z"));
    }

    default LocalDate toDomain(final ZonedDateTime dto) {
        if (dto == null) {
            return null;
        }
        return LocalDate.from(dto);
    }
}
