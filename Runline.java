/**
 * Created by ian on 5/9/15.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Runline extends Encoder {
    private static final ArrayList<Byte> BYTEDECK = genByteDeck();

    public static void main(String[] args) {
        int times = 1;
//        File encoded = encode(new File(args[0]));
        File encoded = encode(new File("TestFiles/gon.png"));

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
        byte[] flags = null;
        byte[] nonEncodedBytes = null;
        ArrayList<Byte> encodedBitStream = null;
        File toReturn = null;

        try {
            System.out.print("Initializing flags: ");
            encodedBitStream = new ArrayList<Byte>();
            nonEncodedBytes = Files.readAllBytes(toEncode.toPath());
            int overflowBytes = 0;
            if (nonEncodedBytes.length % 255 != 0) {
                overflowBytes++;
            }
            flags = new byte[(nonEncodedBytes.length / 255) + overflowBytes];
            genFlags(flags, nonEncodedBytes);

            System.out.println("\n");
            for (byte flag: flags) {
                System.out.println(String.format("%8s", Integer.toBinaryString(flag & 0xFF)).replace(' ', '0'));
            }

            byte[] size = ByteBuffer.allocate(4).putInt(flags.length).array();
            for (byte element: size) {
                encodedBitStream.add(element);
            }

            System.out.println(flags.length + " Flags Completed");
            System.out.print("Encoding Bytes: ");


            for (int i = 0; i < flags.length; i++) {
                encodedBitStream.add(flags[i]);
                byte saveByte = nonEncodedBytes[i * 255];
                int numBytes = 1;
                System.out.println(encodedBitStream.size() - 1);
                for (int j = 1; j <= 255
                        && j + (i * 255) < nonEncodedBytes.length; j++) {
                    if (saveByte == nonEncodedBytes[j + (i * 255)] && numBytes < 255) {
                        numBytes++;
                    } else {
                        if (numBytes > 3 || numBytes >= 255) {
                            encodedBitStream.add(flags[i]);
                            encodedBitStream.add(BYTEDECK.get((numBytes - 1)));
                            encodedBitStream.add(saveByte);
                        } else {
                            for (int k = 0; k < numBytes; k++) {
                                encodedBitStream.add(saveByte);
                            }
                        }
                        numBytes = 1;
                        saveByte = nonEncodedBytes[j + (i * 255)];
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
            int numChunks = ByteBuffer.wrap(bytes).getInt(0);
            ArrayList<Byte> decodedStream = new ArrayList<Byte>();
            byte flag = bytes[4];
            int loc = 5;
            System.out.print("Decoding Stream: ");
            System.out.println("\n");
            for (int i = 0; i < numChunks; i++) {
                System.out.println(String.format("%8s", Integer.toBinaryString(flag & 0xFF)).replace(' ', '0'));
                for (int j = 0; j < 255 && loc < bytes.length; j++) {
                    if (bytes[loc] == flag && loc + 3 < bytes.length) {
                        byte numBytes = bytes[loc + 1];
                        byte data = bytes[loc + 2];
                        int num = 0;
                        for (int l = 0; l < BYTEDECK.size(); l++) {
                            if (numBytes == BYTEDECK.get(l)) {
                                num = l + 1;
                                break;
                            }
                        }

                        for (int k = 0; k < num; k++) {
                            decodedStream.add(data);
                        }
                        loc += 3;
                        j += num - 1;
                    } else {
                        decodedStream.add(bytes[loc]);
                        loc++;
                    }
                }

                if (loc < bytes.length) {
                    System.out.println(loc);
                    flag = bytes[loc];
                    loc++;
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
     * Creates the flag for every 255 bytes
     * @param chunk the chunk of 255 bytes that a flag gonna be generated for
     * @throws Exception throws an exception if there is no key to be found
     * @return the flag for that chunk of bytes
     */
    private static byte genFlag(byte[] chunk)
            throws Exception {
        HashMap<Byte, Integer> flagTable = new HashMap<Byte, Integer>();
        for (byte deck: BYTEDECK) {
            flagTable.put(deck, 0);
        }
        for (byte element: chunk) {
            flagTable.replace(element, flagTable.get(element) + 1);
        }
        for (Map.Entry<Byte, Integer> element: flagTable.entrySet()) {
            if (element.getValue() == 0) {
                return element.getKey();
            }
        }
        throw new Exception("Couldn't Find a key");
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

    /**
     * Generates the flags for the chunks
     * @param flags the array where the flags are to be added
     * @param bitStream the stream of bits from the file
     */
    private static void genFlags(byte[] flags, byte[] bitStream) {
        try {
            int loc = 0;
            ArrayList<Byte> chunkArrayList = new ArrayList<Byte>();
            for (int i = 0; i < flags.length; i++) {
                for (int j = 0; j < 255; j++) {
                    if (loc + j < bitStream.length) {
                        chunkArrayList.add(bitStream[loc + j]);
                    }
                }
                byte[] chunk = new byte[chunkArrayList.size()];
                for (int j = 0; j < chunk.length; j++) {
                    chunk[j] = chunkArrayList.get(j);
                }
                flags[i] = genFlag(chunk);
                chunkArrayList.clear();
                loc += 255;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
