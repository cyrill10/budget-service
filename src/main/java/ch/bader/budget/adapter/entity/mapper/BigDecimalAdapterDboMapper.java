package ch.bader.budget.adapter.entity.mapper;

import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper
public abstract class BigDecimalAdapterDboMapper {

    public BigDecimal mapToBigDecimal(final String s) {
        if (s == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s);
    }

    public String mapToDboString(final BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.toString();
    }
}
