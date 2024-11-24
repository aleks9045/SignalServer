package SignalServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DataUtils {
    public static byte[][] receiveDataBytes(InputStream in) {
        try {
            int numOfBlocks = fourByteArrayToInt(in.readNBytes(4));
            byte[][] blocks = new byte[numOfBlocks][];
            for (int i = 0; i < numOfBlocks; i++) {
                // Читаем длину блока
                byte[] lengthBytes = in.readNBytes(4);
                int blockLength = fourByteArrayToInt(lengthBytes);

                byte[] blockData = new byte[blockLength];
                int bytesRead = 0;
                while (bytesRead < blockLength) {
                    int result = in.read(blockData, bytesRead, blockLength - bytesRead);
                    if (result == -1) {
                        throw new IOException("Connection closed during block transmission.");
                    }
                    bytesRead += result;
                }
                blocks[i] = blockData;
            }
            return blocks;
        } catch (IOException e) {
            System.out.println("Error receiving data bytes.");
        }
        return null;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] shortToByteArray(short value) {
        return new byte[]{
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static int fourByteArrayToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new NumberFormatException();
        }
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    public static int twoByteArrayToInt(byte[] bytes) {
        if (bytes.length != 2) {
            throw new NumberFormatException();
        }
        return ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
    }
}
