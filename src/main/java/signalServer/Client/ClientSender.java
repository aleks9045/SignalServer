package signalServer.Client;


import signalServer.DataUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSender {


    public static void sendData(byte[][] data, int dataType, OutputStream out) throws IOException {
        out.write((byte) dataType);

        out.write(DataUtils.intToByteArray(data.length));
        for (byte[] dataBlock : data) {
            out.write(DataUtils.intToByteArray(dataBlock.length));
            out.write(dataBlock);
            out.flush();
        }
    }

    public static void broadcastBlockchain(Socket sender, byte[][] blockchain) {
        for (Socket socket : ClientRegistry.getClients()) {
            if (socket != sender) {
                try {
                    sendData(blockchain, 1, socket.getOutputStream());
                } catch (IOException e) {
                    System.err.println("Error sending blockchain: " + e.getMessage());
                }
            }
        }
        System.out.println("Broadcast blockchain");
    }

    public static void broadcastTransactionPull(Socket sender, byte[][] pull) {
        for (Socket socket : ClientRegistry.getClients()) {
            if (socket != sender) {
                try {
                    sendData(pull, 2, socket.getOutputStream());
                } catch (IOException e) {
                    System.err.println("Error sending transaction pull: " + e.getMessage());
                }
            }
        }
        System.out.println("Broadcast transaction pull");
    }
}
