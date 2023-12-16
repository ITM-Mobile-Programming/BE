package com.example.mobileprogramming.diary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
public class ResMBTIRateDto {
    private Long ERate;
    private Long IRate;
    private Long SRate;
    private Long NRate;
    private Long FRate;
    private Long TRate;
    private Long PRate;
    private Long JRate;

    @Builder
    public ResMBTIRateDto(Long ERate, Long IRate, Long SRate, Long NRate, Long FRate, Long TRate, Long PRate, Long JRate) {
        this.ERate = ERate;
        this.IRate = IRate;
        this.SRate = SRate;
        this.NRate = NRate;
        this.FRate = FRate;
        this.TRate = TRate;
        this.PRate = PRate;
        this.JRate = JRate;
    }
}
