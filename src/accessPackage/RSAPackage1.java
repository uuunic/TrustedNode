package accessPackage;

import java.io.*;
import java.security.interfaces.RSAPublicKey;

/**
 * Created by firefix on 2015/10/4.
 */
public class RSAPackage1 implements Serializable{
    private static final long serialVersionUID = 110L;
    private RSAPublicKey publicKey;

    private String IP;
    private String name;

    public RSAPackage1(String IP, String name, RSAPublicKey pk) {
        this.IP = IP;
        this.name = name;
        this.publicKey = pk;
    }

    public String getName() {
        return this.name;
    }
    public RSAPublicKey getRSAPublicKey() {
        return this.publicKey;
    }
    public String getIP(){
        return this.IP;
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
