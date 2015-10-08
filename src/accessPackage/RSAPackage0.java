package accessPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by firefix on 2015/10/6.
 */
public class RSAPackage0 implements Serializable {
    private String name;
    private String IP;
    private String request;

    public RSAPackage0(String IP, String name, String request) {
        this.name = name;
        this.IP = IP;
        this.request = request;
    }

    public String getRequest() {
        return this.request;
    }

    public String getIP(){
        return this.IP;
    }
    public String getName(){
        return this.name;
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
