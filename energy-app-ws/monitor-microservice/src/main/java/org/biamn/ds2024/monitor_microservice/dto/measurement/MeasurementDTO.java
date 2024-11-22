package org.biamn.ds2024.monitor_microservice.dto.measurement;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDTO {
    private Timestamp timestamp;
    private Double value;
    private UUID deviceId;

    @Override
    public String toString() {
        return "Measurement {" + " timestamp=" + timestamp + ", value=" + value + ", deviceId=" + deviceId + '}';
    }
}
