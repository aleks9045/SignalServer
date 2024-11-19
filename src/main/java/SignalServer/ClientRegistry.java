package SignalServer;

import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRegistry {
    private final List<Socket> clients = new ArrayList<>();

    public void registerClient(Socket clientSocket) {
        clients.add(clientSocket);
    }

    public void removeClient(Socket clientSocket) {
        clients.remove(clientSocket);
    }

    public void sendMessage(Socket targetSocket, byte[] message) {
        if (targetSocket != null) {
            try {
                OutputStream out = targetSocket.getOutputStream();
                out.write(message);
                out.flush();
            } catch (IOException e) {
                System.err.println("Error sending message" + ": " + e.getMessage());
            }
        } else {
            System.err.println("Client not found");
        }
    }
    public void sendBlockchain(byte[][] blockchain, OutputStream out) throws IOException {
        for (byte[] block : blockchain) {
            int blockLength = block.length;

            out.write(DataUtils.intToByteArray(blockLength));
            out.write(block);
            out.flush();
        }
    }
    public void broadcastBlockchain(Socket sender, byte[][] blockchain) {
        for (Socket socket : clients) {
            if (socket != sender) {
                try {
                    sendBlockchain(blockchain, sender.getOutputStream());
                } catch (IOException e) {
                    System.err.println("Error sending message" + ": " + e.getMessage());
                }
            }
        }
    }
}
