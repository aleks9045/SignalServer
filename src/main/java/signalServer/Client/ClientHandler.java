package signalServer.Client;

import signalServer.DataUtils;
import signalServer.Repository.BlockchainRepository;
import signalServer.Repository.TransactionPullRepository;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final HashMap<Integer, Consumer<byte[][]>> commands = new HashMap<>();
    CommandProcessor commandProcessor = new CommandProcessor();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            ClientRegistry.registerClient(clientSocket);
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

            ClientSender.sendData(BlockchainRepository.getBlockchain(), 1, out);
            System.out.println("Sent blockchain to " + clientSocket.getRemoteSocketAddress());
            ClientSender.sendData(TransactionPullRepository.getTransactionPull(),  2, out);
            System.out.println("Sent transaction pull to " + clientSocket.getRemoteSocketAddress());

            while (!isInterrupted()) {
                int dataType = in.read();
                if (commands.get(dataType) == null) {
                    Thread.currentThread().interrupt();
                }
                byte[][] data = DataUtils.receiveDataBytes(in);
                commandProcessor.process(dataType, data);
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
        ClientSender.broadcastBlockchain(clientSocket, blockchain);
    }

    public void broadcastTransactionPull(byte[][] pull) {
        TransactionPullRepository.setTransactionPull(pull);
        ClientSender.broadcastTransactionPull(clientSocket, pull);
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
