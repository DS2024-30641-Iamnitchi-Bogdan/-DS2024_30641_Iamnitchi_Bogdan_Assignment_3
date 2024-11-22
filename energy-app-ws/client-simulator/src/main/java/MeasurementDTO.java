import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
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