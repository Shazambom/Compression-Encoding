/**
 * Created by ian on 5/9/15.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class Runline extends Encoder {
    private static final ArrayList<Byte> BYTEDECK = genByteDeck();
    private static final byte FLAG = BYTEDECK.get(0);

    public static void main(String[] args) {
        int times = 1;
//        File encoded = encode(new File(args[0]));
        File encoded = encode(new File("TestFiles/loremIpsum.txt"));

        for (int i = 0; i < times - 1; i++) {
            encoded = encode(encoded);
        }
        for (int i = 0; i < times; i++) {
            encoded = decode(encoded);
        }


    }

    /**
     * Encodes the file using run-line encoding
     * @param toEncode the file to be encoded
     * @throws IllegalArgumentException if the file is null
     * @return the encoded file or null if errors occur
     *
     */
    public static File encode(File toEncode)
            throws IllegalArgumentException {
        if (toEncode == null) {
            throw new IllegalArgumentException("File was null");
        }
        byte[] nonEncodedBytes = null;
        ArrayList<Byte> encodedBitStream = null;
        File toReturn = null;

        try {
            System.out.print("Reading File: ");
            encodedBitStream = new ArrayList<Byte>();
            nonEncodedBytes = Files.readAllBytes(toEncode.toPath());
            System.out.println("Completed");
            System.out.print("Encoding Bytes: ");

            for (int i = 0; i < nonEncodedBytes.length; i++) {
                byte saveByte = nonEncodedBytes[i];
                boolean isFlag = false;
                if (saveByte == FLAG) {
                    isFlag = true;
                }
                int count = 1;
                while (i < nonEncodedBytes.length - 1 && saveByte == nonEncodedBytes[i + 1]) {
                    count++;
                    i++;
                    if (count >= 255) {
                        break;
                    }
                }
                if (count > 3 || isFlag) {
                    encodedBitStream.add(FLAG);
                    encodedBitStream.add(BYTEDECK.get(count - 1));
                    encodedBitStream.add(saveByte);
                } else {
                    for (int j = 0; j < count; j++) {
                        encodedBitStream.add(saveByte);
                    }
                }

            }
            System.out.println("Completed");

            System.out.print("Writing to file: ");
            toReturn = new File("Encoded.txt");
            byte[] bytes = new byte[encodedBitStream.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = encodedBitStream.get(i);
            }

            FileOutputStream out = new FileOutputStream(toReturn);
            out.write(bytes);
            out.flush();
            out.close();
            System.out.println("Completed");


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Encoding Complete");

        return toReturn;
    }

    /**
     * Decodes the file using huffman encoding
     * The overhead works by the first 4 bytes being the size of tree
     * The next number bytes afterwards are the nodes in preorder
     * @param toDecode the file to be decoded
     * @throws IllegalArgumentException if the file is null
     * @return the decoded file or null if errors occur
     *
     */
    public static File decode(File toDecode)
            throws IllegalArgumentException {
        if (toDecode == null) {
            throw new IllegalArgumentException("File was null");
        }
        File toReturn = null;



        try {
            System.out.print("Reading Bytes: ");
            byte[] bytes =  Files.readAllBytes(toDecode.toPath());
            System.out.println("Completed");
            ArrayList<Byte> decodedStream = new ArrayList<Byte>();
            System.out.print("Decoding Stream: ");
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == FLAG && i + 3 < bytes.length) {
                    byte countByte = bytes[i + 1];
                    byte data = bytes[i + 2];
                    i += 2;
                    int count;
                    for (count = 0; count < BYTEDECK.size(); count++) {
                        if (countByte == BYTEDECK.get(count)) {
                            count++;
                            break;
                        }
                    }
                    for (int j = 0; j < count; j++) {
                        decodedStream.add(data);
                    }
                } else {
                    decodedStream.add(bytes[i]);
                }
            }

            System.out.println("Completed");
            toReturn = new File("Decoded.txt");
            System.out.print("Writing to file: ");
            byte[] decodedArray = new byte[decodedStream.size()];
            for (int i = 0; i < decodedArray.length; i++) {
                decodedArray[i] = decodedStream.get(i);
            }
            FileOutputStream out = new FileOutputStream(toReturn);
            out.write(decodedArray);
            out.flush();
            out.close();
            System.out.println("Completed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Decoding Complete");
        return toReturn;
    }


    /**
     * Generates the Byte deck with Nodes for the priority queues
     * @return the Array List of the byte Deck
     */
    private static ArrayList<Byte> genByteDeck() {
        ArrayList<Byte> toReturn = new ArrayList<Byte>();
        byte i = Byte.MIN_VALUE;
        while (true) {
            toReturn.add(i);
            if (i == Byte.MAX_VALUE) {
                break;
            }
            i++;
        }
        return toReturn;
    }



}
