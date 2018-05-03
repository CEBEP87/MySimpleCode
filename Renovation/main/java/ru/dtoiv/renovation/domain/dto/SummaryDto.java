package ru.XXXXXXXXX.renovation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class SummaryDto {

    private Integer total;
    private Integer toCheck;
    private Integer toAgreement;
    private Integer confirmed;
}
