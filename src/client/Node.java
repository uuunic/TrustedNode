package client;

import encryption.AES;

import java.security.interfaces.RSAPublicKey;

/**
 * Created by firefix on 2015/9/28.
 */
public class Node {
    private String IP = null;
    private String name = null;

    private long lastHeartBeat;
    private boolean isValid = false;
    private boolean isOnline = false;

    private RSAPublicKey RSAPublicKey;

    public Node(String IP, String name) {
        this.IP = IP;
        this.name = name;
    }
    public synchronized void updateLastHeartBeatTime() {
        this.lastHeartBeat = System.currentTimeMillis();
    }

    public RSAPublicKey getRSAPublicKey(){
        return this.RSAPublicKey;
    }
    public long getLastHeartBeatTime() {
        return this.lastHeartBeat;
    }
    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
        updateLastHeartBeatTime();
    }
    public void setPublicKey(RSAPublicKey pk) {
        this.RSAPublicKey = pk;
        return;
    }
    public String getIP() {
        return this.IP;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isOnline(){
        return isOnline;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

}
