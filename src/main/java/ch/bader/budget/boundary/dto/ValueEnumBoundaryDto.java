package ch.bader.budget.boundary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValueEnumBoundaryDto {
    private String name;
    private Integer value;
}
