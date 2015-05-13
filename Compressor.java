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
//        File toCompress = new File(args[0]);
        File toCompress = new File("TestFiles/big.txt");

        for (int i = 0; i < times; i++) {
            toCompress = Huffman.encode(toCompress);
        }
        for (int i = 0; i < times; i++) {
            toCompress = Runline.encode((toCompress));
        }
        for (int i = 0; i < times; i++) {
            toCompress = Runline.decode(toCompress);
        }
        for (int i = 0; i < times; i++) {
            toCompress = Huffman.decode(toCompress);
        }


    }
}
