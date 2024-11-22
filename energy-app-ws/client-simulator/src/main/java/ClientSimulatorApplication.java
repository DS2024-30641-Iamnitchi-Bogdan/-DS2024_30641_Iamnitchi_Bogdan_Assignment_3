import java.util.TimeZone;

public class ClientSimulatorApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Scheduler.start(args);
    }
}
