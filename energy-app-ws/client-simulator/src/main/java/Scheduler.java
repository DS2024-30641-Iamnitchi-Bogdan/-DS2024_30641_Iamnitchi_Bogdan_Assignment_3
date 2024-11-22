import jdk.vm.ci.meta.Local;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Scheduler {

    static int index = 0; // index of the message to send

    public static void start(String[] args) {

        UUID deviceId = UUID.fromString(args[0]);
        int period = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        LocalDateTime dateTime = LocalDateTime.now();
        List<String> values = Reader.readData("sensor.csv");

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MeasurementDTO m = new MeasurementDTO(
                    Timestamp.valueOf(dateTime.plusMinutes(index * 10L)),
                    Double.valueOf(values.get(index)),
                    deviceId
                );
                try {
                    Producer.sendMessage(m);
                    index++;
                } catch (IOException | TimeoutException | URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        }, 0, period);
    }
}
