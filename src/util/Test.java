package util;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by firefix on 2015/9/28.
 */
public class Test {
    public static void main(String[] args) {

        byte[] b = new byte[1024];
        int [] a = new int[256];
        for(int i=0; i<a.length; i++) {
            a[i] = 3123513 * i;
            System.out.print(a[i] + " ");
        }
        System.out.println();
        for(int i=0; i<a.length; i++) {
            b[i * 4] = (byte)(a[i] >>> 24);
            b[i * 4 + 1] = (byte)((a[i] >>> 16) & 0x000000ff);
            b[i * 4 + 2] = (byte)((a[i] >>> 8) & 0x000000ff);
            b[i * 4 + 3] = (byte)((a[i]) & 0x000000ff);
        }
        for(int i=0; i<b.length; i++) {
            System.out.print(b[i] + " ");
        }
        System.out.println();
        int[] integer = new int[256];
        for (int i=0; i<integer.length; i++) {
            integer[i] = (int) ((b[i * 4 + 3] & 0xFF)
                    | ((b[i * 4 + 2] & 0xFF)<<8)
                    | ((b[i * 4 + 1] & 0xFF)<<16)
                    | ((b[i * 4] & 0xFF)<<24));
        }
        for(int i=0; i<integer.length; i++) {
            System.out.print(integer[i] + " ");
        }
        System.out.println();


    }

}
