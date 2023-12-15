package ch.bader.budget.adapter.entity.mapper;

import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper
public interface BigDecimalAdapterDboMapper {

    default BigDecimal mapToBigDecimal(final String s) {
        if (s == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s);
    }

    default String mapToDboString(final BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.toString();
    }
}
