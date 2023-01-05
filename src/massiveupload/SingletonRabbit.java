package massiveupload;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *
 * @author luishure
 */
public final class SingletonRabbit {
    
    private String USER_NAME = "guest";
    private String PASSWORD = "guest";
    private String HOST = "localhost";
    private String VIRTUAL_HOST = "corevida.vh";
    private int PORT = 5672;
    
    private static SingletonRabbit instance;
    public Channel channel;
    
    private SingletonRabbit() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);
        factory.setHost(HOST);
        factory.setVirtualHost(VIRTUAL_HOST);
        factory.setPort(PORT);
        
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
    }
    
    public static SingletonRabbit getInstance() throws Exception {
        if (instance == null) {
            instance = new SingletonRabbit();
        }
        return instance;
    }
    
    public void publishMessage(String exchangeName, String routingKey, String message) throws Exception {
        this.channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
    }
    
}
