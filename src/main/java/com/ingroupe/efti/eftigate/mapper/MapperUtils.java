package com.ingroupe.efti.eftigate.mapper;

import com.ingroupe.efti.eftigate.dto.ControlDto;
import com.ingroupe.efti.eftigate.dto.ErrorDto;
import com.ingroupe.efti.eftigate.dto.RequestDto;
import com.ingroupe.efti.eftigate.entity.ControlEntity;
import com.ingroupe.efti.eftigate.entity.ErrorEntity;
import com.ingroupe.efti.eftigate.entity.RequestEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapperUtils {

    private final ModelMapper modelMapper;

    public ControlEntity controlDtoToControEntity(final ControlDto controlDto) {
        final ControlEntity controlEntity = modelMapper.map(controlDto, ControlEntity.class);

        //ça marche pas sinon
        if (controlDto.getError() != null) {
            final ErrorEntity errorEntity = new ErrorEntity();
            errorEntity.setErrorCode(controlDto.getError().getErrorCode());
            errorEntity.setErrorDescription(controlDto.getError().getErrorDescription());
            errorEntity.setId(controlDto.getError().getId());
            controlEntity.setError(errorEntity);
        }

        return controlEntity;
    }

    public ControlDto controlEntityToControlDto(final ControlEntity controlEntity) {

        return modelMapper.map(controlEntity, ControlDto.class);
    }

    public RequestEntity requestDtoToRequestEntity(final RequestDto requestDto) {
        return modelMapper.map(requestDto, RequestEntity.class);
    }

    public RequestDto requestToRequestDto(final RequestEntity requestEntity) {
        return modelMapper.map(requestEntity, RequestDto.class);
    }
}
