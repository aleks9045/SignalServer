import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        int port = 8000; // Укажите нужный порт
        SignalingServer server = new SignalingServer(new InetSocketAddress(port));
        server.start();
        System.out.println("Signaling server running on port: " + port);
    }
}
