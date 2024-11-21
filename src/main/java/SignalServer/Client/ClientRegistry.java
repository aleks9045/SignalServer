package SignalServer.Client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRegistry {
    private static volatile List<Socket> clients = new ArrayList<>();

    public static synchronized List<Socket> getClients() {
        return clients;
    }

    public static synchronized void registerClient(Socket clientSocket) {
        clients.add(clientSocket);
    }

    public static synchronized void removeClient(Socket clientSocket) {
        clients.remove(clientSocket);
    }
}
