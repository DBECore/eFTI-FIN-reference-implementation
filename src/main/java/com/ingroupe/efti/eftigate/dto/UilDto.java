package com.ingroupe.efti.eftigate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UilDto {
    @NotNull
    private String gate;
    @NotNull
    private String uuid;
    @NotNull
    private String platform;
}
