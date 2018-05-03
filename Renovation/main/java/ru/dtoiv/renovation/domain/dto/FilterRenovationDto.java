package ru.XXXXXXXXX.renovation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;

import java.util.List;
@AllArgsConstructor
@Setter
@Getter
public class FilterRenovationDto {

private List<Renovation> collection;
private SummaryDto summary;
}
