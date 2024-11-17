package SignalServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {
    private final Map<String, Socket> clients = new ConcurrentHashMap<>();

    public void registerClient(String clientId, Socket clientSocket) {
        clients.put(clientId, clientSocket);
    }

    public void removeClient(String clientId) {
        clients.remove(clientId);
    }

    public void sendMessage(String targetClientId, String message) {
        Socket targetSocket = clients.get(targetClientId);
        if (targetSocket != null) {
            try {
                PrintWriter out = new PrintWriter(targetSocket.getOutputStream(), true);
                System.out.println("Sending message to " + targetClientId);
                out.println(message);
            } catch (IOException e) {
                System.err.println("Error sending message" + ": " + e.getMessage());
            }
        } else {
            System.err.println("Client " + targetClientId + " not found");
        }
    }
}
