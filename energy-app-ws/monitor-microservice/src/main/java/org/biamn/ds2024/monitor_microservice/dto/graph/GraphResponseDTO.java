package org.biamn.ds2024.monitor_microservice.dto.graph;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphResponseDTO {
    private String[] hours;
    private Double[] values;
}
