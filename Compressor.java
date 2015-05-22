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
        int hTimes = 1;
        int rlTimes = 0;
        int lzTimes = 0;
//        File toCompress = new File(args[0]);
        File toCompress = new File("TestFiles/big.txt");

        for (int i = 0; i < hTimes; i++) {
            toCompress = Huffman.encode(toCompress);
        }
        for (int i = 0; i < rlTimes; i++) {
            toCompress = Runline.encode((toCompress));
        }
        for (int i = 0; i < rlTimes; i++) {
            toCompress = Runline.decode(toCompress);
        }
        for (int i = 0; i < hTimes; i++) {
            toCompress = Huffman.decode(toCompress);
        }


    }
}
