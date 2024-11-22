package org.biamn.ds2024.monitor_microservice.dto.device;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponseDTO {

    private UUID id;
    private String name;
    private String location;
    private Double max_consumption;
    private UUID user_id;
}
