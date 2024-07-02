package com.ingroupe.efti.eftigate.dto;

import com.ingroupe.efti.eftigate.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotesRequestDto extends RequestDto {
    private String note;
    private String eFTIPlatformUrl;
    public NotesRequestDto(final ControlDto controlDto) {
        super(controlDto);
        this.setRequestType(RequestType.NOTE);
        this.setNote(controlDto.getNotes());
        this.setEFTIPlatformUrl(controlDto.getEftiPlatformUrl());
    }
}
