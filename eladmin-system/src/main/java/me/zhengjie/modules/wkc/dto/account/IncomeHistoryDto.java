package me.zhengjie.modules.wkc.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeHistoryDto {
    private Double totalIncome;

    private Integer nextPage;

    @JsonProperty("incomeArr")

    private List<IncomeDto> incomes;
}