package com.ingroupe.efti.eftigate.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.ingroupe.common.test.log.MemoryAppender;
import com.ingroupe.efti.commons.enums.EDeliveryAction;
import com.ingroupe.efti.edeliveryapconnector.exception.SendRequestException;
import com.ingroupe.efti.edeliveryapconnector.service.RequestSendingService;
import com.ingroupe.efti.eftigate.config.GateProperties;
import com.ingroupe.efti.eftigate.dto.RequestDto;
import com.ingroupe.efti.eftigate.exception.TechnicalException;
import com.ingroupe.efti.eftigate.service.request.RequestServiceFactory;
import com.ingroupe.efti.eftigate.service.request.UilRequestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RabbitListenerServiceTest extends AbstractServiceTest{
    @Mock
    private ControlService controlService;
    @Mock
    private RequestSendingService requestSendingService;
    @Mock
    private RequestServiceFactory requestServiceFactory;
    @Mock
    private UilRequestService uilRequestService;
    @Mock
    private ApIncomingService apIncomingService;

    private final static String url = "url";
    private final static String password = "password";
    private final static String username = "username";

    private RabbitListenerService rabbitListenerService;

    private MemoryAppender memoryAppender;

    private Logger memoryAppenderTestLogger;

    @Mock
    private Function<RequestDto, EDeliveryAction> requestToEDeliveryActionFunction;
    private static final String LOGGER_NAME = RabbitListenerService.class.getName();


    @BeforeEach
    void before() {

        final GateProperties gateProperties = GateProperties.builder()
                .owner("http://france.lol")
                .ap(GateProperties.ApConfig.builder()
                        .url(url)
                        .password(password)
                        .username(username).build()).build();

        rabbitListenerService = new RabbitListenerService(controlService, gateProperties, serializeUtils, requestSendingService, requestServiceFactory, apIncomingService, requestToEDeliveryActionFunction);
        memoryAppenderTestLogger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        memoryAppender =
                MemoryAppender.createInitializedMemoryAppender(
                        Level.TRACE, memoryAppenderTestLogger);
    }

    @AfterEach
    public void cleanupLogAppenderForTest() {
        MemoryAppender.shutdownMemoryAppender(memoryAppender, memoryAppenderTestLogger);
    }


    @Test
    void listenMessageReceiveDeadQueueTest() {
        final String message = "oki";

        rabbitListenerService.listenMessageReceiveDeadQueue(message);

        assertTrue(memoryAppender.containedInFormattedLogMessage(message));
        assertEquals(1,memoryAppender.countEventsForLogger(LOGGER_NAME, Level.ERROR));
    }

    @Test
    void listenReceiveMessageTest() {
        final String message = "{\"id\":0,\"journeyStart\":\"2024-01-26T10:54:51+01:00\",\"countryStart\":\"FR\",\"journeyEnd\":\"2024-01-27T10:54:51+01:00\",\"countryEnd\":\"FR\",\"metadataUUID\":\"032ad16a-ce1b-4ed2-a943-3b3975be9148\",\"transportVehicles\":[{\"id\":0,\"transportMode\":\"ROAD\",\"sequence\":1,\"vehicleId\":null,\"vehicleCountry\":\"FR\",\"journeyStart\":\"2024-01-26T10:54:51+01:00\",\"countryStart\":\"FR\",\"journeyEnd\":\"2024-01-27T10:54:51+01:00\",\"countryEnd\":\"FRANCE\"}],\"dangerousGoods\":false,\"disabled\":false,\"eFTIGateUrl\":null,\"eFTIDataUuid\":\"032ad16a-ce1b-4ed2-a943-3b3975be9169\",\"eFTIPlatformUrl\":\"http://efti.platform.acme.com\"}";

        rabbitListenerService.listenReceiveMessage(message);

        assertTrue(memoryAppender.containedInFormattedLogMessage(message));
        assertEquals(1,memoryAppender.countEventsForLogger(LOGGER_NAME, Level.INFO));
    }

    @Test
    void listenReceiveMessageExceptiontest() {
        final String message = "ça va pétteeeeer";

        final Exception exception = assertThrows(TechnicalException.class, () -> {
            rabbitListenerService.listenReceiveMessage(message);
        });

        assertEquals("Error when try to map class com.ingroupe.efti.edeliveryapconnector.dto.ReceivedNotificationDto with message : ça va pétteeeeer", exception.getMessage());
    }

    @Test
    void listenSendMessageTest() {
        when(requestServiceFactory.getRequestServiceByRequestType(any())).thenReturn(uilRequestService);

        final String message = "{\"id\":151,\"status\":\"RECEIVED\",\"edeliveryMessageId\":null,\"retry\":0,\"reponseData\":null,\"nextRetryDate\":null,\"createdDate\":[2024,3,5,15,6,52,135892300],\"lastModifiedDate\":null,\"gateUrlDest\":\"http://efti.gate.borduria.eu\",\"control\":{\"id\":102,\"eftiDataUuid\":\"12345678-ab12-4ab6-8999-123456789abe\",\"requestUuid\":\"c5ed0840-bf60-4052-8172-35530d423672\",\"requestType\":\"LOCAL_UIL_SEARCH\",\"status\":\"PENDING\",\"eftiPlatformUrl\":\"http://efti.platform.acme.com\",\"eftiGateUrl\":\"http://efti.gate.borduria.eu\",\"subsetEuRequested\":\"SubsetEuRequested\",\"subsetMsRequested\":\"SubsetMsRequested\",\"createdDate\":[2024,3,5,15,6,51,987861600],\"lastModifiedDate\":[2024,3,5,15,6,51,987861600],\"eftiData\":null,\"transportMetaData\":null,\"fromGateUrl\":null,\"requests\":null,\"authority\":{\"id\":99,\"country\":\"SY\",\"legalContact\":{\"id\":197,\"email\":\"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn.A@63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.commmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm\",\"streetName\":\"rue des rossignols\",\"buildingNumber\":\"12\",\"city\":\"Acheville\",\"additionalLine\":null,\"postalCode\":\"62320\"},\"workingContact\":{\"id\":198,\"email\":\"toto@gmail.com\",\"streetName\":\"rue des cafés\",\"buildingNumber\":\"14\",\"city\":\"Lille\",\"additionalLine\":\"osef\",\"postalCode\":\"59000\"},\"isEmergencyService\":null,\"name\":\"aaaa\",\"nationalUniqueIdentifier\":\"aaa\"},\"error\":null,\"metadataResults\":null},\"error\":null}";

        rabbitListenerService.listenSendMessage(message);

        assertTrue(memoryAppender.containedInFormattedLogMessage("receive message from rabbimq queue"));
        assertEquals(1,memoryAppender.countEventsForLogger(LOGGER_NAME, Level.INFO));
    }

    @Test
    void listenSendMessageSameGateTest() {
        when(requestServiceFactory.getRequestServiceByRequestType(any())).thenReturn(uilRequestService);
        final String message = "{\"id\":151,\"status\":\"RECEIVED\",\"edeliveryMessageId\":null,\"retry\":0,\"reponseData\":null,\"nextRetryDate\":null,\"createdDate\":[2024,3,5,15,6,52,135892300],\"lastModifiedDate\":null,\"gateUrlDest\":\"http://france.lol\",\"control\":{\"id\":102,\"eftiDataUuid\":\"12345678-ab12-4ab6-8999-123456789abe\",\"requestUuid\":\"c5ed0840-bf60-4052-8172-35530d423672\",\"requestType\":\"LOCAL_UIL_SEARCH\",\"status\":\"PENDING\",\"eftiPlatformUrl\":\"http://efti.platform.acme.com\",\"eftiGateUrl\":\"http://efti.gate.borduria.eu\",\"subsetEuRequested\":\"SubsetEuRequested\",\"subsetMsRequested\":\"SubsetMsRequested\",\"createdDate\":[2024,3,5,15,6,51,987861600],\"lastModifiedDate\":[2024,3,5,15,6,51,987861600],\"eftiData\":null,\"transportMetaData\":null,\"fromGateUrl\":null,\"requests\":null,\"authority\":{\"id\":99,\"country\":\"SY\",\"legalContact\":{\"id\":197,\"email\":\"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn.A@63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.commmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm\",\"streetName\":\"rue des rossignols\",\"buildingNumber\":\"12\",\"city\":\"Acheville\",\"additionalLine\":null,\"postalCode\":\"62320\"},\"workingContact\":{\"id\":198,\"email\":\"toto@gmail.com\",\"streetName\":\"rue des cafés\",\"buildingNumber\":\"14\",\"city\":\"Lille\",\"additionalLine\":\"osef\",\"postalCode\":\"59000\"},\"isEmergencyService\":null,\"name\":\"aaaa\",\"nationalUniqueIdentifier\":\"aaa\"},\"error\":null,\"metadataResults\":null},\"error\":null}";

        rabbitListenerService.listenSendMessage(message);
    }

    @Test()
    void listenSendMessageFailedBuildRequestApRequestDtoTest() {
        final String message = "oki";

        final Exception exception = assertThrows(TechnicalException.class, () -> {
            rabbitListenerService.listenSendMessage(message);
        });

        assertEquals("Error when try to map class com.ingroupe.efti.eftigate.dto.RequestDto with message : oki", exception.getMessage());
    }

    @Test
    void listenSendMessageFailedSendDomibusTest() {
        final String message = "{\"id\":151,\"status\":\"RECEIVED\",\"edeliveryMessageId\":null,\"retry\":0,\"reponseData\":null,\"nextRetryDate\":null,\"createdDate\":[2024,3,5,15,6,52,135892300],\"lastModifiedDate\":null,\"gateUrlDest\":\"http://efti.gate.borduria.eu\",\"control\":{\"id\":102,\"eftiDataUuid\":\"12345678-ab12-4ab6-8999-123456789abe\",\"requestUuid\":\"c5ed0840-bf60-4052-8172-35530d423672\",\"requestType\":\"LOCAL_UIL_SEARCH\",\"status\":\"PENDING\",\"eftiPlatformUrl\":\"http://efti.platform.acme.com\",\"eftiGateUrl\":\"http://efti.gate.borduria.eu\",\"subsetEuRequested\":\"SubsetEuRequested\",\"subsetMsRequested\":\"SubsetMsRequested\",\"createdDate\":[2024,3,5,15,6,51,987861600],\"lastModifiedDate\":[2024,3,5,15,6,51,987861600],\"eftiData\":null,\"transportMetaData\":null,\"fromGateUrl\":null,\"requests\":null,\"authority\":{\"id\":99,\"country\":\"SY\",\"legalContact\":{\"id\":197,\"email\":\"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn.A@63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.commmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm\",\"streetName\":\"rue des rossignols\",\"buildingNumber\":\"12\",\"city\":\"Acheville\",\"additionalLine\":null,\"postalCode\":\"62320\"},\"workingContact\":{\"id\":198,\"email\":\"toto@gmail.com\",\"streetName\":\"rue des cafés\",\"buildingNumber\":\"14\",\"city\":\"Lille\",\"additionalLine\":\"osef\",\"postalCode\":\"59000\"},\"isEmergencyService\":null,\"name\":\"aaaa\",\"nationalUniqueIdentifier\":\"aaa\"},\"error\":null,\"metadataResults\":null},\"error\":null}";
        when(requestSendingService.sendRequest(any(), any())).thenThrow(SendRequestException.class);

        final Exception exception = assertThrows(TechnicalException.class, () -> {
            rabbitListenerService.listenSendMessage(message);
        });

        assertEquals("Error when try to send message to domibus", exception.getMessage());
    }

    @Test
    void listenSendMessageDeadLetterTest() {
        when(requestServiceFactory.getRequestServiceByRequestType(any())).thenReturn(uilRequestService);
        final String message = "{\"id\":151,\"status\":\"RECEIVED\",\"edeliveryMessageId\":null,\"retry\":0,\"reponseData\":null,\"nextRetryDate\":null,\"createdDate\":[2024,3,5,15,6,52,135892300],\"lastModifiedDate\":null,\"gateUrlDest\":\"http://efti.gate.borduria.eu\",\"control\":{\"id\":102,\"eftiDataUuid\":\"12345678-ab12-4ab6-8999-123456789abe\",\"requestUuid\":\"c5ed0840-bf60-4052-8172-35530d423672\",\"requestType\":\"LOCAL_UIL_SEARCH\",\"status\":\"PENDING\",\"eftiPlatformUrl\":\"http://efti.platform.acme.com\",\"eftiGateUrl\":\"http://efti.gate.borduria.eu\",\"subsetEuRequested\":\"SubsetEuRequested\",\"subsetMsRequested\":\"SubsetMsRequested\",\"createdDate\":[2024,3,5,15,6,51,987861600],\"lastModifiedDate\":[2024,3,5,15,6,51,987861600],\"eftiData\":null,\"transportMetaData\":null,\"fromGateUrl\":null,\"requests\":null,\"authority\":{\"id\":99,\"country\":\"SY\",\"legalContact\":{\"id\":197,\"email\":\"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn.A@63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.63ccccccccccccccccccccccccccccccccccccccccccccccccccccccccgmail.commmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm\",\"streetName\":\"rue des rossignols\",\"buildingNumber\":\"12\",\"city\":\"Acheville\",\"additionalLine\":null,\"postalCode\":\"62320\"},\"workingContact\":{\"id\":198,\"email\":\"toto@gmail.com\",\"streetName\":\"rue des cafés\",\"buildingNumber\":\"14\",\"city\":\"Lille\",\"additionalLine\":\"osef\",\"postalCode\":\"59000\"},\"isEmergencyService\":null,\"name\":\"aaaa\",\"nationalUniqueIdentifier\":\"aaa\"},\"error\":null,\"metadataResults\":null},\"error\":null}";

        rabbitListenerService.listenSendMessageDeadLetter(message);

        assertTrue(memoryAppender.containedInFormattedLogMessage("Receive message for dead queue"));
        assertEquals(1,memoryAppender.countEventsForLogger(LOGGER_NAME, Level.ERROR));
    }
}
