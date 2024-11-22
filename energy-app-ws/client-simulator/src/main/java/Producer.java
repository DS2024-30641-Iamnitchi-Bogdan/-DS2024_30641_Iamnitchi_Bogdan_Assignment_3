import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class Producer {

    private static final String HOST = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");
    private static final String EXCHANGE_NAME = System.getenv().getOrDefault("RABBITMQ_EXCHANGE_NAME", "simulator-topic-exchange");

    public static void sendMessage(MeasurementDTO m) throws IOException, TimeoutException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(m);

        try(Connection connection = factory.newConnection()){
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);

            channel.basicPublish(EXCHANGE_NAME, "", false, null, json.getBytes());
            System.out.println("Send: " + m.toString());
        }
    }
}
