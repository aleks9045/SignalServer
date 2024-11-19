package SignalServer;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ClientRegistry clientRegistry;
    private final HashMap<Byte, Consumer<byte[][]>> commands = new HashMap<>();
    CommandProcessor commandProcessor = new CommandProcessor();
    private byte[][] currentBlockchain = new byte[][]{{
        10, 64, 50, 97, 99, 57, 97, 54,
                55, 52, 54, 97, 99, 97, 53, 52, 51,
                97, 102, 56, 100, 102, 102, 51, 57,
                56, 57, 52, 99, 102, 101, 56, 49, 55,
                51, 97, 102, 98, 97, 50, 49, 101, 98,
                48, 49, 99, 54, 102, 97, 101, 51, 51,
                100, 53, 50, 57, 52, 55, 50, 50, 50, 56,
            53, 53, 101, 102, 18, 1, 48, 26, 0}};
    private byte[] currentTransactionPull = new byte[0];

    public ClientHandler(Socket clientSocket, ClientRegistry clientRegistry) {
        this.clientSocket = clientSocket;
        this.clientRegistry = clientRegistry;
    }

    @Override
    public void run() {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            clientRegistry.registerClient(clientSocket);
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            Thread.sleep(1000);
            clientRegistry.sendBlockchain(currentBlockchain, out);
//            clientRegistry.sendMessage(clientSocket, currentTransactionPull);

            while (!isInterrupted()) {
                byte[] request;
                while ((request = in.readAllBytes()) != null) {
                    commandProcessor.process(request[0]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            clientRegistry.removeClient(clientSocket);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
    public void broadcastBlockchain(byte[][] blockchain) {
        currentBlockchain = blockchain;
        clientRegistry.broadcastBlockchain(clientSocket, currentBlockchain);
    }
//    public void broadcastTransactionPull(byte[] pull) {
//        currentTransactionPull = pull;
//        clientRegistry.broadcastMessage(clientSocket, currentTransactionPull);
//    }
    private class CommandProcessor {
        public CommandProcessor() {
            commands.put((byte) 1, ClientHandler.this::broadcastBlockchain);
//            commands.put((byte) 2, ClientHandler.this::broadcastTransactionPull);
        }

        public void process(byte type) {
            Consumer<byte[][]> cmd = commands.get(type);
            if (cmd != null) {
                cmd.accept(currentBlockchain);
            } else {
                throw new IllegalArgumentException("Unknown command: " + type);
            }
        }
    }
}
