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


public class Huffman {
    /**
     * Is the Main method used for testing
     * @param args the arguments that are useless cause they aren't used
     */
    public static void main(String[] args) {

        decode(encode(new File("audio.txt")));
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

            while (weightPairs.size() > 1) {
                Node first = weightPairs.poll();
                Node second = weightPairs.poll();
                Node combined = new Node(first.getWeight() + second.getWeight(), (byte) '\0', first, second);
                weightPairs.add(combined);
            }
            head = weightPairs.poll();
            traverse(head, "", huffmanMap);


            int k = 0;
            int g = 0;
            int length = nonEncodedBytes.length;
            String encodedStream = "";
            for (byte element: nonEncodedBytes) {
                encodedStream += huffmanMap.get(element);
                k++;
                if (k % (length / 100) == 0) {
                    g++;
                    System.out.println(g + "%");
                }
            }
            ArrayList<Byte> arrayListBytes = new ArrayList<Byte>();
            ArrayList<Byte> overhead = new ArrayList<Byte>();
            initOverhead(overhead, huffmanMap);

            //Adding the overhead
            byte[] size = ByteBuffer.allocate(4).putInt(overhead.size()).array();
            for (byte element: size) {
                arrayListBytes.add(element);
            }
            byte[] streamSize = ByteBuffer.allocate(4).putInt(encodedStream.length()).array();
            for (byte element: streamSize) {
                arrayListBytes.add(element);
            }
            while (encodedStream.length() % 8 != 0) {
                encodedStream += "0";
            }
            for (byte element: overhead) {
                arrayListBytes.add(element);
            }
            //Adding the endoded bytes
            for (byte element: new BigInteger(encodedStream, 2).toByteArray()) {
                arrayListBytes.add(element);
            }
            toReturn = new File("Encoded.txt");
            byte[] bytes = new byte[arrayListBytes.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = arrayListBytes.get(i);
            }

            //writing to the file
            FileOutputStream out = new FileOutputStream(toReturn);
            out.write(bytes);
            out.flush();
            out.close();

        } catch (Exception e) {
            System.out.println("Fuck 2.0");
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
            bytes = Files.readAllBytes(encoded.toPath());
            String bitString = "";
            for (byte element: bytes) {
                bitString += String.format("%8s", Integer.toBinaryString(element & 0xFF)).replace(' ', '0');
            }

            int size = ByteBuffer.wrap(bytes).getInt(0);
            int streamSize = ByteBuffer.wrap(bytes).getInt(4);


            HashMap<Byte, String> huffmanMap = new HashMap<Byte, String>();
            regenMap(huffmanMap, bytes, size);
            HashMap<String, Byte> reverseHuffmanMap = new HashMap<String, Byte>();
            //fix this later, this is what it should be decoded to directly but I'm lazy right now
            for (Map.Entry<Byte, String> element: huffmanMap.entrySet()) {
                reverseHuffmanMap.put(element.getValue(), element.getKey());
            }
            int bitLoc = (8 * 8) + (size * 8);
            String tempString = "";
            ArrayList<Byte> decodedBytes = new ArrayList<Byte>();
            for (int i = bitLoc; i < streamSize + bitLoc; i++) {
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
            FileOutputStream out = new FileOutputStream(decoded);
            out.write(decodedArray);
            out.flush();
            out.close();

        } catch (Exception e) {
            System.out.println("Fuck 3.0");
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
        for (int i = 8; i < size + 8; i++) {
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


    private static class Node implements
            Comparable<Node>{
        private int weight;
        private byte value;
        private Node left;
        private Node right;


        /**
         * Initializes the object
         * @param weight the weight of the char
         * @param value the value of the char
         */
        public Node(int weight, byte value) {
            this.weight = weight;
            this.value = value;
            left = null;
            right = null;
        }

        /**
         * Initializes the object
         * @param weight the weight of the char
         * @param value the value of the char
         * @param left the left child of the node
         * @param right the right child of the node
         */
        public Node(int weight, byte value, Node left, Node right) {
            this.weight = weight;
            this.value = value;
            this.left = left;
            this.right = right;
        }


        /**
         * Gets the left child node
         * @return the left variable
         */
        public Node getLeft() {
            return left;
        }

        /**
         * Sets the left child
         * @param left the node to be set to left
         */
        public void setLeft(Node left) {
            this.left = left;
        }

        /**
         * Gets the right child node
         * @return the right variable
         */
        public Node getRight() {
            return right;
        }

        /**
         * Sets the right child
         * @param right the node to be set to right
         */
        public void setRight(Node right) {
            this.right = right;
        }

        /**
         * Gets the weight
         * @return the weight of the char
         */
        public int getWeight() {
            return weight;
        }

        /**
         * Sets the weight variable
         * @param weight the new value of weight
         */
        public void setWeight(int weight) {
            this.weight = weight;
        }
        /**
         * Gets the value
         * @return the value of the char
         */
        public byte getValue() {
            return value;
        }

        /**
         * Increases the weight of the Node
         */
        public void increaseWeight() {
            weight++;
        }

        @Override
        public int compareTo(Node other) {
            if (weight < other.getWeight()) {
                return -1;
            } else if (weight > other.getWeight()) {
                return 1;
            } else {
                return ((int) value) - ((int) other.getValue());
            }
        }
    }


}
