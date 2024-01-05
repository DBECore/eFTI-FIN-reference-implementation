package com.ingroupe.platform.platformgatesimulator.controller;

import com.ingroupe.efti.edeliveryapconnector.dto.ReceivedNotificationDto;
import com.ingroupe.efti.edeliveryapconnector.exception.SendRequestException;
import com.ingroupe.platform.platformgatesimulator.service.ApIncomingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Random;

import static java.lang.Thread.sleep;

@RestController
@RequestMapping("/ws")
@AllArgsConstructor
@Slf4j
public class ApIncomingController {

    private final ApIncomingService apIncomingService;

    private final int maxSleep = 20 * 1000;
    private final int minSleep = 1 * 1000;

    @PostMapping("/notification")
    public void getById(final @RequestBody ReceivedNotificationDto receivedNotificationDto) throws IOException, InterruptedException {
        log.info("Notification reçus");
        int rand = new Random().nextInt(maxSleep-minSleep)+minSleep;
        sleep(rand);
        apIncomingService.manageIncomingNotification(receivedNotificationDto);
    }
}
