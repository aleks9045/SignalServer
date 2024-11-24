package SignalServer.Client;

import SignalServer.DataUtils;
import SignalServer.Repository.BlockchainRepository;
import SignalServer.Repository.TransactionPullRepository;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;

import static SignalServer.DataUtils.twoByteArrayToInt;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ClientSender clientSender;
    private final HashMap<Integer, Consumer<byte[][]>> commands = new HashMap<>();
    CommandProcessor commandProcessor = new CommandProcessor();

    public ClientHandler(Socket clientSocket, ClientSender clientSender) {
        this.clientSocket = clientSocket;
        this.clientSender = clientSender;
    }

    @Override
    public void run() {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            ClientRegistry.registerClient(clientSocket);
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

            clientSender.sendData(BlockchainRepository.getBlockchain(), 1, out);
            System.out.println("Sent blockchain to " + clientSocket.getRemoteSocketAddress());
            clientSender.sendData(TransactionPullRepository.getTransactionPull(),  2, out);
            System.out.println("Sent transaction pull to " + clientSocket.getRemoteSocketAddress());

            while (!isInterrupted()) {
                int messageType = twoByteArrayToInt(in.readNBytes(2));
                if (commands.get(messageType) == null) {
                    sendError(out);
                    continue;
                }
                byte[][] data = DataUtils.receiveDataBytes(in);
                commandProcessor.process(messageType, data);
            }
        } catch (IOException e) {
            System.err.println("Error handling client " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
        } finally {
            ClientRegistry.removeClient(clientSocket);
            try {
                clientSocket.close();
                System.out.println("Socket closed: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    public void broadcastBlockchain(byte[][] blockchain) {
        BlockchainRepository.setBlockchain(blockchain);
        clientSender.broadcastBlockchain(clientSocket, blockchain);
    }

    public void broadcastTransactionPull(byte[][] pull) {
        TransactionPullRepository.setTransactionPull(pull);
        clientSender.broadcastTransactionPull(clientSocket, pull);
    }

    public void sendError(OutputStream outputStream) {
        clientSender.sendError(outputStream);
        System.out.println("Sent Error");
    }

    private class CommandProcessor {
        public CommandProcessor() {
            commands.put(1, ClientHandler.this::broadcastBlockchain);
            commands.put(2, ClientHandler.this::broadcastTransactionPull);
        }

        public void process(int type, byte[][] data) {
            Consumer<byte[][]> cmd = commands.get(type);
            cmd.accept(data);
        }
    }
}
