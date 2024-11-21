package SignalServer.Client;


import SignalServer.DataUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSender {

    public void sendError(OutputStream out) {
        try {
            out.write(DataUtils.shortToByteArray((short) 0));
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public void sendData(byte[][] data, int type, OutputStream out) throws IOException {
        out.write(DataUtils.shortToByteArray((short) type));
        out.write(DataUtils.intToByteArray(data.length));
        for (byte[] dataBlock : data) {
            int blockLength = dataBlock.length;
            System.out.println(dataBlock.length);
            out.write(DataUtils.intToByteArray(blockLength));
            out.write(dataBlock);
            out.flush();
        }
    }

    public void broadcastBlockchain(Socket sender, byte[][] blockchain) {
        for (Socket socket : ClientRegistry.getClients()) {
            if (socket != sender) {
                try {
                    sendData(blockchain, 1, sender.getOutputStream());
                } catch (IOException e) {
                    System.err.println("Error sending blockchain: " + e.getMessage());
                }
            }
        }
        System.out.println("Broadcast blockchain");
    }

    public void broadcastTransactionPull(Socket sender, byte[][] pull) {
        for (Socket socket :  ClientRegistry.getClients()) {
            if (socket != sender) {
                try {
                    sendData(pull, 2, sender.getOutputStream());
                } catch (IOException e) {
                    System.err.println("Error sending transaction pull: " + e.getMessage());
                }
            }
        }
        System.out.println("Broadcast transaction pull");
    }
}
