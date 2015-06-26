import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ian on 5/26/15.
 */
public class LZ78 extends Encoder{

    public static File encode(File toEncode)throws IllegalArgumentException {
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







        } catch (Exception e) {
            e.printStackTrace();
        }



        return toReturn;
    }

    public static File decode(File toDecode)throws IllegalArgumentException {
        if (toDecode == null) {
            throw new IllegalArgumentException("File was null");
        }
        return null;
    }
}
