package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ScannedTransactionBoundaryDto;
import ch.bader.budget.domain.ScannedTransaction;
import org.mapstruct.Mapper;

import java.time.YearMonth;

@Mapper(uses = {CardTypeBoundaryDtoMapper.class, ClosingProcessBoundaryDtoMapper.class}, imports = YearMonth.class)
public interface ScannedTransactionBoundaryDtoMapper {

    ScannedTransactionBoundaryDto mapToDto(ScannedTransaction domain);

//    ScannedTransaction mapToDomain(ScannedTransactionDbo entity);
//
//    ScannedTransactionDbo mapToEntity(ScannedTransaction domain);
}
