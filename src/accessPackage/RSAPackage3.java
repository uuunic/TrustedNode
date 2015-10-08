package accessPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by firefix on 2015/10/6.
 */
public class RSAPackage3 implements Serializable {
    private String IP;
    private String name;
    private byte[] aesKey;

    public RSAPackage3(String IP, String name, byte[] aesKey) {
        this.IP = IP;
        this.name = name;
        this.aesKey = aesKey;
    }
    public String getIP() {
        return IP;
    }
    public String getName() {
        return name;
    }
    public byte[] getAESKey() {
        return aesKey;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(this);
            byte[] result = os.toByteArray();
            os.close();
            oos.close();
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Cannot get ObjectOutputStream from RSAPackage1.");
        }
    }
}
