/**
 * Created by ian on 4/22/15.
 */


import java.io.File;
import java.util.PriorityQueue;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.util.Map;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.math.BigInteger;


public class Huffman extends Encoder{
    /**
     * Is the Main method used for testing
     * @param args the arguments that are useless cause they aren't used
     */
    public static void main(String[] args) {
        int times = 1;
//        File encoded = encode(new File(args[0]));
        File encoded = encode(new File("TestFiles/FitnessProject.txt"));

        for (int i = 0; i < times - 1; i++) {
            encoded = encode(encoded);
        }
        for (int i = 0; i < times; i++) {
            encoded = decode(encoded);
        }
    }


    //String.format("%8s", Integer.toBinaryString(byteToPrint & 0xFF)).replace(' ', '0')
    //^^^^^^^^^^Gives the string of the binary of a byte^^^^^^^^^^^

    /**
     * Encodes the file using huffman encoding
     * The overhead works by the first 4 bytes being the size of tree
     * The next number bytes afterwards are the nodes in preorder
     * @param toEncode the file to be encoded
     * @throws IllegalArgumentException if the file is null
     * @return the encoded file or null if errors occur
     *
     */
    public static File encode(File toEncode)
            throws IllegalArgumentException{
        if (toEncode == null) {
            throw new IllegalArgumentException("File was null");
        }
        InputStream inStream = null;
        BufferedInputStream in = null;
        PriorityQueue<Node> weightPairs = new PriorityQueue<Node>();
        HashMap<Byte, String> huffmanMap = new HashMap<Byte, String>();
        Node head = null;
        byte[] nonEncodedBytes = null;
        File toReturn = null;

        try {
            nonEncodedBytes = Files.readAllBytes(toEncode.toPath());
            //----------------------------Use this ^ to rework it --------------------------
            System.out.print("Creating huffman weight table... ");
            for (byte theByte: nonEncodedBytes) {
                boolean didIncrease = false;
                for (Node element : weightPairs) {
                    if (element.getValue() == theByte) {
                        //could be the source of a few bugs such as
                        // the priority queue not restructuring
                        element.increaseWeight();
                        didIncrease = true;
                    }
                }
                if (!didIncrease) {
                    weightPairs.add(new Node(1, theByte));
                }
            }
           //---------------------------------------------------------------------------------
            System.out.println("Completed");
            System.out.print("Creating huffman tree... ");
            while (weightPairs.size() > 1) {
                Node first = weightPairs.poll();
                Node second = weightPairs.poll();
                Node combined = new Node(first.getWeight() + second.getWeight(), (byte) '\0', first, second);
                weightPairs.add(combined);
            }
            System.out.println("Completed");
            head = weightPairs.poll();
            System.out.print("Traversing huffman tree... ");
            traverse(head, "", huffmanMap);
            System.out.println("Completed");

            System.out.print("Encoding new bit stream... ");
            StringBuilder encodedStream = new StringBuilder();
            for (byte element: nonEncodedBytes) {
                encodedStream.append(huffmanMap.get(element));
            }
            System.out.println("Completed");
            ArrayList<Byte> arrayListBytes = new ArrayList<Byte>();
            ArrayList<Byte> overhead = new ArrayList<Byte>();
            System.out.print("Generating overhead... ");
            initOverhead(overhead, huffmanMap);

            //Adding the overhead
            byte[] size = ByteBuffer.allocate(4).putInt(overhead.size()).array();
            for (byte element: size) {
                arrayListBytes.add(element);
            }
            while (encodedStream.length() % 8 != 0) {
                encodedStream.append("0");
            }
            for (byte element: overhead) {
                arrayListBytes.add(element);
            }
            System.out.println("Completed");
            System.out.print("Adding new encoded bit stream... ");
            convert(encodedStream, arrayListBytes);
            System.out.println("Completed");
            toReturn = new File("Encoded.txt");
            byte[] bytes = new byte[arrayListBytes.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = arrayListBytes.get(i);
            }

            System.out.print("Writing to the file... ");
            //writing to the file
            FileOutputStream out = new FileOutputStream(toReturn);
            out.write(bytes);
            out.flush();
            out.close();

            System.out.println("Completed");


        } catch (Exception e) {
            System.out.println("Fuck");
            e.printStackTrace();
        }
        System.out.println("Encoding Complete");
        return toReturn;
    }


    /**
     * Decodes the file using huffman encoding
     * The overhead works by the first 4 bytes being the size of tree
     * The next number bytes afterwards are the nodes in preorder
     * @param encoded the file to be decoded
     * @throws IllegalArgumentException if the file is null
     * @return the decoded file or null if errors occur
     *
     */
    public static File decode(File encoded)
            throws IllegalArgumentException{
        if (encoded == null) {
            throw new IllegalArgumentException("The file was null");
        }
        File decoded = new File("Decoded.txt");
        byte[] bytes = null;

        try {
            System.out.print("Analyzing bytes... ");
            bytes = Files.readAllBytes(encoded.toPath());
            StringBuilder bitString = new StringBuilder();
            for (byte element: bytes) {
                bitString.append(String.format("%8s", Integer.toBinaryString(element & 0xFF)).replace(' ', '0'));
            }

            int size = ByteBuffer.wrap(bytes).getInt(0);


            HashMap<Byte, String> huffmanMap = new HashMap<Byte, String>();
            regenMap(huffmanMap, bytes, size);
            HashMap<String, Byte> reverseHuffmanMap = new HashMap<String, Byte>();
            //fix this later, this is what it should be decoded to directly but I'm lazy right now
            for (Map.Entry<Byte, String> element: huffmanMap.entrySet()) {
                reverseHuffmanMap.put(element.getValue(), element.getKey());
            }
            System.out.println("Completed");
            int bitLoc = (4 * 8) + (size * 8);


            String tempString = "";
            System.out.print("Decoding Bytes... ");
            ArrayList<Byte> decodedBytes = new ArrayList<Byte>();
            for (int i = bitLoc; i < bitString.length(); i++) {
                if (reverseHuffmanMap.containsKey(tempString)) {
                    decodedBytes.add(reverseHuffmanMap.get(tempString));
                    tempString = "";
                    i--;
                } else {
                    tempString += bitString.charAt(i);
                }
            }
            byte[] decodedArray = new byte[decodedBytes.size()];
            for (int i = 0; i < decodedArray.length; i++) {
                decodedArray[i] = decodedBytes.get(i);
            }
            System.out.println("Completed");
            System.out.print("Writing to file... ");
            FileOutputStream out = new FileOutputStream(decoded);
            out.write(decodedArray);
            out.flush();
            out.close();
            System.out.println("Completed");

        } catch (Exception e) {
            System.out.println("Fuck");
            e.printStackTrace();
        }
        System.out.println("Decoding Complete");
        return decoded;
    }

    /**
     * Initializes the Overhead of huffman
     * @param overhead the arraylist of bytes to be added
     * @param huffmanMap the map of all of the huffman codes
     */
    private static void initOverhead(ArrayList<Byte> overhead, HashMap<Byte, String> huffmanMap) {
        for (Map.Entry<Byte, String> element: huffmanMap.entrySet()) {
            byte key = element.getKey();
            String val = element.getValue();
            byte length = (byte) val.length();
            overhead.add(key);
            overhead.add(length);
            while (val.length() % 8 != 0) {
                val += "0";
            }
            byte[] bytes = new BigInteger(val, 2).toByteArray();
            int i = 0;
            for (byte value: bytes) {
                if (i != 0 || value != 0 || val.equals("00000000")) {
                    overhead.add(value);
                }
                i++;
            }

        }
    }

    /**
     * Regenerates the HashMap of the huffman code
     * @param huffmanMap the map that all the values will be put in
     * @param bytes the byte array of the file
     * @param size the size of the stream of bytes in the overhead
     */
    private static void regenMap(HashMap<Byte, String> huffmanMap, byte[] bytes, int size) {
        //first byte is the key value
        //second byte is the length of the bit string needed for decoding
        //third-however many is the decoding bit string
        //size + 8 is the index where the first byte of the stream starts
        boolean key = true;
        boolean length = false;
        byte keyVal = 0;
        byte lengthVal = 0;
        for (int i = 4; i < size + 4; i++) {
            if (key) {
                keyVal = bytes[i];
                key = false;
                length = true;
            } else if (length) {
                lengthVal = bytes[i];
                if (lengthVal < 0) {
                    lengthVal *= (-1);
                }
                length = false;
            } else {
                byte lengthTemp = lengthVal;
                while (lengthVal % 8 != 0) {
                    lengthVal++;
                }
                int numBytes = lengthVal / 8;

                String value = "";
                for (int j = 0; j < numBytes; j++) {
                    value += String.format("%8s", Integer.toBinaryString(bytes[j + i] & 0xFF)).replace(' ', '0');
                }
                value = value.substring(0, lengthTemp);
                huffmanMap.put(keyVal, value);
                i += numBytes - 1;
                key = true;
            }
        }
    }


    /**
     * Converts the binary string to a byte array
     * @param binaryString the string of bits to be converted
     * @param bytes the arrayList that the bytes are to be added to
     */
    private static void convert(StringBuilder binaryString, ArrayList<Byte> bytes) {
        // assumes binaryString.length() is a multiple of 8
        byte data = 0;
        for (int i = 0; i < binaryString.length() / 8; i++) {
            int minIndex = i * 8;
            int maxIndex = (i + 1) * 8;
            for (int j = minIndex; j < maxIndex; j++) {
                data <<= 1;
                data |= binaryString.charAt(j) - '0';
            }
            bytes.add(data);
        }
    }

    /**
     * Traverses the huffman tree and maps each char to a bit value
     * @param current the current node
     * @param bits the bit value
     * @param huffmanMap the map that is the guide to encode
     */
    private static void traverse(Node current, String bits, HashMap<Byte, String> huffmanMap) {
        if (current.getLeft() == null && current.getRight() == null) {
            huffmanMap.put(current.getValue(), bits);
        } else {
            if (current.getLeft() != null) {
                traverse(current.getLeft(), bits + "0", huffmanMap);
            }
            if (current.getRight() != null) {
                traverse(current.getRight(), bits + "1", huffmanMap);
            }
        }
    }

    /**
     * Converts a byte array to an int
     * @param b the byte array
     * @return the int derived from the byte array
     */
    private static int byteArrayToInt(byte[] b)
    {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * Converts an int to a byte array
     * @param a the int
     * @return the byte array representing the int
     */
    private static byte[] intToByteArray(int a)
    {
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

}
