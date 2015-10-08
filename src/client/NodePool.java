package client;

import Packet.PacketThreadManager;
import accessPackage.RSAPackage0;
import accessPackage.RSAPackage1;
import accessPackage.RSAPackage2;
import accessPackage.RSAPackage3;
import encryption.MD5Util;
import encryption.RSAUtil;
import util.Log;
import util.Tools;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by firefix on 2015/9/28.
 */
public class NodePool {
    private static final String TAG = "NodePool";
    private HashMap<String, Node> nodeMap = new HashMap<String, Node>();
    private HashMap<String, byte[]> AESMap = new HashMap<String, byte[]>();
    private HashMap<String, RSAPublicKey> RSAMap = new HashMap<String, RSAPublicKey>();

    private RSAUtil rsa;
    private RSAPrivateKey sk;

    public NodePool(){
        rsa = new RSAUtil();
        try {
            sk = rsa.getRSAPrivateKey();
            RSAPublicKey pk = rsa.getRSAPublicKey();

            AESMap.put(PacketThreadManager.getLocalIP(), MD5Util.hash(getLocalNode().getIP().getBytes()));
            RSAMap.put(PacketThreadManager.getLocalIP(), pk);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Node getNode(String IP){
        return nodeMap.get(IP);
    }
    public Node getNodeByName(String name){
        Iterator it = nodeMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            Node node = (Node)entry.getValue();
            if(node.getName().equals(name)){
                return node;
            }
        }
        return null;
    }
    public boolean addNode(String IP, Node node){
        if(!node.getIP().equals(IP)) {
            Log.e(getClass().getName(), "node and IP is Not match");
            throw new IllegalArgumentException();

        } else {
            nodeMap.put(IP, node);
            Log.i("[NodePool || setNode()]", " IP =" + IP);
            return true;
        }
    }
    public boolean setNodeIsValid(String IP, boolean isValid) {
        Node node = getNode(IP);
        if(node == null) {
            Log.e(TAG, "IP :" + IP + " is NOT in the NodePool");
            return false;
        }else {
            node.setIsValid(isValid);
            return true;
        }
    }
    public boolean updateLastHeartBeatTime(String IP){
        Node node = nodeMap.get(IP);
        if(node == null) {
            Log.e(TAG, "updateLastHeartBeat error, node is not exist. IP :" + IP);
            return false;
        }else {
            node.updateLastHeartBeatTime();
            return true;
        }
    }

    public Node getLocalNode(){
        Node node = nodeMap.get(PacketThreadManager.getLocalIP());
        if(node == null) {
            Log.e(TAG, "CANNOT get local node, the local IP is not in the nodePool. Start Test Model !");
            node = new Node(PacketThreadManager.getLocalIP(), "TestLocalNode");
        } else {
        }
        return node;
    }
    public void setAESKey(String IP, byte[] key) {
        AESMap.put(IP, key);
    }
    public byte[] getAESKey(String IP) {
        return AESMap.get(IP);
    }
    public byte[] getAESKey(Node node) {
        return AESMap.get(node.getIP());
    }

    public void setRSAPublicKey(String IP, RSAPublicKey pk) {
        RSAMap.put(IP, pk);
    }
    public RSAPublicKey getRSAPublicKey(String IP) {
        return RSAMap.get(IP);
    }

    public Iterator<String> keyIterator(){
        return nodeMap.keySet().iterator();
    }
    public Iterator iterator(){
        return nodeMap.entrySet().iterator();
    }

    public RSAPackage0 startAuth(){
        Node local = getLocalNode();
        String request = local.getName() + "wants to start authentication.";
        return new RSAPackage0(local.getIP(), local.getName(), request);
    }

    public RSAPackage0 clientStartAuth(){
        return new RSAPackage0(getLocalNode().getIP(), getLocalNode().getName(), "Mission Start.");
    }
    public RSAPackage1 requestForRSAPublicKey(RSAPackage0 package0) {
        Node local = getLocalNode();
        RSAPublicKey pk;
        if(RSAMap.get(local.getIP()) != null && sk != null) {
            pk = RSAMap.get(local.getIP());

            return new RSAPackage1(local.getIP(), local.getName(), pk);
        } else {
            Log.e(TAG, "Get local PublicKey error! Can't find the key");
            RSAPublicKey rsaPublicKey = null;
            try {
                rsaPublicKey = rsa.getRSAPublicKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            RSAMap.put(local.getIP(), rsaPublicKey);
            try {
                sk = rsa.getRSAPrivateKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new RSAPackage1(local.getIP(), local.getName(), rsaPublicKey);

        }
    }
    public RSAPackage2 requestForServerAESKey(RSAPackage1 package1){
        Node local = getLocalNode();
        String sourceIP = package1.getIP();
        String sourceName = package1.getName();
        RSAPublicKey pk_other = package1.getRSAPublicKey();
        RSAMap.put(sourceIP, pk_other);


        byte[] localAESKey = Tools.generateRandomByteArray(); //local端的AES key 用 other端的RSA PK 加密发送出去
        AESMap.put(sourceIP, localAESKey);

        byte[] aesKeyEncrypt = null;
        try {
            aesKeyEncrypt = rsa.encrypt(pk_other, localAESKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RSAPublicKey pk_mine = RSAMap.get(local.getIP());
        return new RSAPackage2(local.getIP(), local.getName(), pk_mine, aesKeyEncrypt);
    }

    public RSAPackage3 callbackToClient(RSAPackage2 package2) {
        Node local = getLocalNode();
        String sourceIP = package2.getIP();
        String sourceName = package2.getName();
        RSAPublicKey pk_other = package2.getRSAPublicKey();
        byte[] aesKey_other_before = package2.getAESKey();

        byte[] aesKey_other_after = null;
        try {
            aesKey_other_after = rsa.decrypt(sk, aesKey_other_before);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AESMap.put(sourceIP, aesKey_other_after);
        RSAMap.put(sourceIP, pk_other);

        byte[] aesKey_local_encrypt = null;
        try {
            aesKey_local_encrypt = rsa.encrypt(pk_other, aesKey_other_after);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RSAPackage3(local.getIP(), local.getName(), aesKey_local_encrypt);

    }
    public boolean clientAuthEnd(RSAPackage3 p3) {
        String sourceIP = p3.getIP();
        String name = p3.getName();
        byte[] aesKey_other_before = p3.getAESKey();
        byte[] aesKey_other_after = null;
        try {
            aesKey_other_after = rsa.decrypt(sk, aesKey_other_before);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(aesKey_other_after == null) {
            Log.e(TAG, "Client can't auth to " + name + ", AES Key is null !");
            return false;
        } else if(!Arrays.equals(aesKey_other_after, AESMap.get(sourceIP))) {
            Log.e(TAG, "AES Key is not match  !");
            return false;
        } else {
            return true;
        }
    }
}
