package eu.efti.eftigate.entity;
import eu.efti.commons.enums.CountryIndicator;
import eu.efti.commons.enums.TransportMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransportVehicleEntity extends JourneyEntity implements Serializable {

    private int id;
    @Enumerated(EnumType.STRING)
    private TransportMode transportMode;
    private int sequence;
    private String vehicleID;
    @Enumerated(EnumType.STRING)
    private CountryIndicator vehicleCountry;
}