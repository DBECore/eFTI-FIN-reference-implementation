package eu.efti.edeliveryapconnector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationContentDto {
    private String messageId;
    private String contentType;
    private String fromPartyId;
    private String body;
    private String conversationId;
}
