package ch.bader.budget.boundary.dto.mapper;


import ch.bader.budget.boundary.dto.ClosingProcessBoundaryDto;
import ch.bader.budget.domain.ClosingProcess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.YearMonth;

@Mapper(componentModel = "jakarta-cdi", uses = ClosingProcessStatusBoundaryDtoMapper.class, imports = YearMonth.class)
public interface ClosingProcessBoundaryDtoMapper {

    @Mapping(target = "year", expression = "java(domain.getYearMonth().getYear())")
    @Mapping(target = "month", expression = "java(domain.getYearMonth().getMonthValue()-1)")
    ClosingProcessBoundaryDto mapToDto(ClosingProcess domain);

//    ClosingProcess mapToDomain(ClosingProcessDbo entity);
//
//    ClosingProcessDbo mapToEntity(ClosingProcess domain);
}
