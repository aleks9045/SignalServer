package SignalServer;

import SignalServer.Client.ClientHandler;
import SignalServer.Client.ClientSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private final ClientSender clientSender = new ClientSender();


    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Signaling server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientSender);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error starting signaling server: " + e.getMessage());
        }
    }
}