import java.io.File;

/**
 * Created by ian on 5/9/15.
 */
public abstract class Encoder {
    /**
     * Encodes the file some sort of encoding algorithm
     * @param toEncode the file to be encoded
     * @throws IllegalArgumentException if the file is null
     * @return the encoded file or null if errors occur
     */
    public static File encode(File toEncode)throws IllegalArgumentException {
        return null;
    }

    /**
     * Decodes the file using the same algorithm
     * @param toDecode the file to be decoded
     * @throws IllegalArgumentException if the file is null
     * @return the encoded file or null if errors occur
     */
    public static File decode(File toDecode)throws IllegalArgumentException {
        return null;
    }
}
