package signalServer.Repository;

public class TransactionPullRepository {
    private static volatile byte[][] transactionPull = {{}};

    public static synchronized byte[][] getTransactionPull() {
        return transactionPull;
    }

    public static synchronized void setTransactionPull(byte[][] newTransactionPull) {
        transactionPull = newTransactionPull;
    }
}