package com.ingroupe.platform.platformgatesimulator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ingroupe.efti.commons.dto.MetadataDto;
import com.ingroupe.platform.platformgatesimulator.dto.UploadMetadataDto;
import com.ingroupe.platform.platformgatesimulator.service.ApIncomingService;
import com.ingroupe.platform.platformgatesimulator.service.ReaderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/metadata")
@AllArgsConstructor
@Slf4j
public class MetadataController {

    private final ApIncomingService apIncomingService;

    private final ReaderService readerService;

    @PostMapping("/upload/file")
    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file) {
        if (file == null) {
            log.error("No file send");
            return new ResponseEntity("Error, no file send", HttpStatus.BAD_REQUEST);
        }
        log.info("try to upload file");
        readerService.uploadFile(file);
        return new ResponseEntity<>("File saved",HttpStatus.OK);
    }

    @PostMapping("/upload/metadata")
    public ResponseEntity<String> uploadMetadata(@RequestBody MetadataDto metadataDto) {
        if (metadataDto == null) {
            log.error("Error no metadata send");
            return new ResponseEntity<>("No metadata send", HttpStatus.BAD_REQUEST);
        }
        log.info("send metadata to gate");
        try {
            apIncomingService.uploadMetadata(metadataDto);
        } catch (JsonProcessingException e) {
            log.error("Error when try to send to gate the Metadata", e);
        }
        return new ResponseEntity<>("Metadata uploaded", HttpStatus.OK);
    }
}
