package eu.efti.eftigate.service.request;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import eu.efti.commons.enums.RequestStatusEnum;
import eu.efti.commons.enums.RequestTypeEnum;
import eu.efti.commons.enums.StatusEnum;
import eu.efti.commons.utils.MemoryAppender;
import eu.efti.eftigate.entity.ControlEntity;
import eu.efti.eftigate.entity.IdentifiersRequestEntity;
import eu.efti.eftigate.repository.IdentifiersRequestRepository;
import eu.efti.eftigate.service.BaseServiceTest;
import eu.efti.v1.edelivery.Consignment;
import eu.efti.v1.edelivery.IdentifierResponse;
import eu.efti.v1.edelivery.UIL;
import eu.efti.v1.types.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentifiersControlUpdateDelegateServiceTest extends BaseServiceTest {
    protected final ControlEntity controlEntity = new ControlEntity();
    private final IdentifiersRequestEntity identifiersRequestEntity = new IdentifiersRequestEntity();
    private final IdentifiersRequestEntity secondIdentifiersRequestEntity = new IdentifiersRequestEntity();

    private IdentifiersControlUpdateDelegateService identifiersControlUpdateDelegateService;
    @Mock
    private IdentifiersRequestRepository identifiersRequestRepository;
    @Captor
    private ArgumentCaptor<IdentifiersRequestEntity> requestEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<ControlEntity> controlEntityArgumentCaptor;

    @Override
    @BeforeEach
    public void before() {
        super.before();
        super.setEntityRequestCommonAttributes(identifiersRequestEntity);
        super.setEntityRequestCommonAttributes(secondIdentifiersRequestEntity);
        controlEntity.setRequests(List.of(identifiersRequestEntity));
        identifiersControlUpdateDelegateService = new IdentifiersControlUpdateDelegateService(identifiersRequestRepository, controlService, mapperUtils);


        final Logger memoryAppenderTestLogger = (Logger) LoggerFactory.getLogger(IdentifiersRequestService.class);
        memoryAppender = MemoryAppender.createInitializedMemoryAppender(Level.INFO, memoryAppenderTestLogger);
    }

    @Test
    void shouldUpdateExistingControlRequest() {
        final IdentifierResponse identifierResponse = new IdentifierResponse();
        identifierResponse.setRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c");
        final Consignment consignment = new Consignment();
        final UIL uil = new UIL();
        uil.setPlatformId("platformId");
        uil.setGateId("gateId");
        uil.setDatasetId("datasetId");
        consignment.setUil(uil);
        consignment.setCarrierAcceptanceDateTime(new DateTime());
        identifierResponse.getConsignment().add(consignment);
        //Arrange
        identifiersRequestEntity.setStatus(RequestStatusEnum.IN_PROGRESS);
        when(identifiersRequestRepository.findByControlRequestIdAndStatusAndGateIdDest("67fe38bd-6bf7-4b06-b20e-206264bd639c", RequestStatusEnum.IN_PROGRESS, "borduria")).thenReturn(identifiersRequestEntity);
        //Act
        identifiersControlUpdateDelegateService.updateExistingControl(identifierResponse, "borduria");

        //Assert
        verify(identifiersRequestRepository).save(requestEntityArgumentCaptor.capture());
        assertEquals(RequestStatusEnum.SUCCESS, requestEntityArgumentCaptor.getValue().getStatus());
        assertNotNull(requestEntityArgumentCaptor.getValue().getIdentifiersResults());
        assertFalse(requestEntityArgumentCaptor.getValue().getIdentifiersResults().getConsignments().isEmpty());
    }

    @Test
    void shouldGetCompleteAsControlNextStatus_whenAllRequestsAreInSuccessStatus() {
        identifiersRequestEntity.setStatus(RequestStatusEnum.SUCCESS);
        controlEntity.setStatus(StatusEnum.PENDING);
        controlEntity.setRequestType(RequestTypeEnum.EXTERNAL_IDENTIFIERS_SEARCH);
        when(identifiersRequestRepository.findByControlRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(List.of(identifiersRequestEntity));
        when(controlService.findByRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(Optional.of(controlEntity));
        //Act
        identifiersControlUpdateDelegateService.setControlNextStatus("67fe38bd-6bf7-4b06-b20e-206264bd639c");

        //assert
        verify(controlService).save(controlEntityArgumentCaptor.capture());
        assertEquals(StatusEnum.COMPLETE, controlEntityArgumentCaptor.getValue().getStatus());
    }

    @Test
    void shouldGetErrorAsControlNextStatus_whenSomeRequestsAreInErrorStatus() {
        identifiersRequestEntity.setStatus(RequestStatusEnum.ERROR);
        controlEntity.setStatus(StatusEnum.PENDING);
        controlEntity.setRequestType(RequestTypeEnum.EXTERNAL_IDENTIFIERS_SEARCH);
        when(identifiersRequestRepository.findByControlRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(List.of(identifiersRequestEntity, secondIdentifiersRequestEntity));
        when(controlService.findByRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(Optional.of(controlEntity));
        //Act
        identifiersControlUpdateDelegateService.setControlNextStatus("67fe38bd-6bf7-4b06-b20e-206264bd639c");

        //assert
        verify(controlService).save(controlEntityArgumentCaptor.capture());
        assertEquals(StatusEnum.ERROR, controlEntityArgumentCaptor.getValue().getStatus());
    }

    @Test
    void shouldGetTimeoutAsControlNextStatus_whenSomeRequestsAreInTimeoutStatus() {
        secondIdentifiersRequestEntity.setStatus(RequestStatusEnum.TIMEOUT);
        identifiersRequestEntity.setStatus(RequestStatusEnum.IN_PROGRESS);
        controlEntity.setStatus(StatusEnum.PENDING);
        controlEntity.setRequests(List.of(identifiersRequestEntity, secondIdentifiersRequestEntity));
        controlEntity.setRequestType(RequestTypeEnum.EXTERNAL_IDENTIFIERS_SEARCH);
        when(identifiersRequestRepository.findByControlRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(List.of(identifiersRequestEntity, secondIdentifiersRequestEntity));
        when(controlService.findByRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(Optional.of(controlEntity));
        //Act
        identifiersControlUpdateDelegateService.setControlNextStatus("67fe38bd-6bf7-4b06-b20e-206264bd639c");

        //assert
        verify(controlService).save(controlEntityArgumentCaptor.capture());
        assertEquals(StatusEnum.TIMEOUT, controlEntityArgumentCaptor.getValue().getStatus());
    }

    @Test
    void shouldGetErrorAsControlNextStatusOverTimeout_whenAtLeastOneRequestIsInErrorStatus() {
        secondIdentifiersRequestEntity.setStatus(RequestStatusEnum.TIMEOUT);
        identifiersRequestEntity.setStatus(RequestStatusEnum.ERROR);
        controlEntity.setStatus(StatusEnum.PENDING);
        controlEntity.setRequests(List.of(identifiersRequestEntity, secondIdentifiersRequestEntity));
        controlEntity.setRequestType(RequestTypeEnum.EXTERNAL_IDENTIFIERS_SEARCH);
        when(identifiersRequestRepository.findByControlRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(List.of(identifiersRequestEntity, secondIdentifiersRequestEntity));
        when(controlService.findByRequestId("67fe38bd-6bf7-4b06-b20e-206264bd639c")).thenReturn(Optional.of(controlEntity));
        //Act
        identifiersControlUpdateDelegateService.setControlNextStatus("67fe38bd-6bf7-4b06-b20e-206264bd639c");

        //assert
        verify(controlService).save(controlEntityArgumentCaptor.capture());
        assertEquals(StatusEnum.ERROR, controlEntityArgumentCaptor.getValue().getStatus());
    }

}