package encryption;

import java.security.MessageDigest;

/**
 * Created by firefix on 2015/9/25.
 */
public class MD5Util {
    public static final byte[] hash(byte[] input) {
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input);
            return md.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static final String byteArray2Hex (byte[] array) {
        char hexDigits[] = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'A', 'B',
                'C', 'D', 'E', 'F'
        };
        char[] str = new char[array.length * 2];
        int k = 0;
        for(int i = 0 ;i < array.length; i++) {
            byte p = array[i];
            str[k++] = hexDigits[p >>> 4 & 0xf];
            str[i++] = hexDigits[p & 0xf];
        }
        return new String(str);
    }
    public static void main(String args[]) {
        char[] c = {'1','3', 'c'};
        String ct = String.valueOf(c);
        byte[] ctb = MD5Util.hash(ct.getBytes());
        System.out.println(new String(ctb));
        System.out.println(MD5Util.byteArray2Hex(ct.getBytes()));
    }
}