package org.biamn.ds2024.device_microservice.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeviceRequestDTO implements Serializable {

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Location is required")
    @NotBlank(message = "Location is required")
    private String location;

    private String description;

    @NotNull(message = "Avg consumption is required")
    @Positive(message = "Avg consumption must be positive")
    private Double avg_consumption;

    @NotNull(message = "Max consumption is required")
    @Positive(message = "Max consumption must be positive")
    private Double max_consumption;

    @NotNull(message = "User id is required")
    private UUID user_id;

}
