package SignalServer.Repository;

public class BlockchainRepository {
    private static volatile byte[][] blockchain = new byte[][]{{
            10, 64, 50, 97, 99, 57, 97, 54,
            55, 52, 54, 97, 99, 97, 53, 52, 51,
            97, 102, 56, 100, 102, 102, 51, 57,
            56, 57, 52, 99, 102, 101, 56, 49, 55,
            51, 97, 102, 98, 97, 50, 49, 101, 98,
            48, 49, 99, 54, 102, 97, 101, 51, 51,
            100, 53, 50, 57, 52, 55, 50, 50, 50, 56,
            53, 53, 101, 102, 18, 1, 48, 26, 0
    }};

    public static synchronized byte[][] getBlockchain() {
        return blockchain;
    }

    public static synchronized void setBlockchain(byte[][] newBlockchain) {
        blockchain = newBlockchain;
    }
}
