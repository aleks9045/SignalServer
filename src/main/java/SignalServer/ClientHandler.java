package SignalServer;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ClientRegistry clientRegistry;
    private String clientId;

    public ClientHandler(Socket clientSocket, ClientRegistry clientRegistry) {
        this.clientSocket = clientSocket;
        this.clientRegistry = clientRegistry;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientId = in.readLine();
            if (clientId != null) {
                clientRegistry.registerClient(clientId, clientSocket);
                System.out.println("Client connected: " + clientId);
            }
            while (!isInterrupted()) {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] data = line.split(" ");
                    String targetId = data[0];
                    String message = data[1];
                    System.out.println(clientId + " want to say " + targetId + ": " + message);
                    clientRegistry.sendMessage(targetId, message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client " + clientId + ": " + e.getMessage());
        } finally {
            clientRegistry.removeClient(clientId);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
