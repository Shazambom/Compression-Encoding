import java.io.File;

/**
 * Created by ian on 5/13/15.
 */
public class Compressor {
    /**
     * The main method of the whole project
     * @param args the first arg will be the name of the file path to the file to be compressed
     */
    public static void main(String[] args) {
        int times = 1;
//        File encoded = encode(new File(args[0]));
        File encoded = Huffman.encode(new File("TestFiles/big.txt"));

        for (int i = 0; i < times - 1; i++) {
            encoded = Huffman.encode(encoded);
        }
        for (int i = 0; i < times; i++) {
            encoded = Runline.encode((encoded));
        }
        for (int i = 0; i < times; i++) {
            encoded = Runline.decode(encoded);
        }
        for (int i = 0; i < times; i++) {
            encoded = Huffman.decode(encoded);
        }


    }
}
