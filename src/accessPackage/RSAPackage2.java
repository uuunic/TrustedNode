package accessPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.interfaces.RSAPublicKey;

/**
 * Created by firefix on 2015/10/6.
 */
public class RSAPackage2 implements Serializable {
    private String name;
    private String IP;
    private RSAPublicKey pk;
    private byte[] aesKey;
    public RSAPackage2(String IP, String name, RSAPublicKey pk, byte[] aesKey) {
        this.IP = IP;
        this.name = name;
        this.pk = pk;
        this.aesKey = aesKey;
    }

    public String getIP() {
        return IP;
    }
    public String getName() {
        return name;
    }
    public RSAPublicKey getRSAPublicKey() {
        return pk;
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
