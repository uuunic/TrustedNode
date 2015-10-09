/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataHandler.audio;

import ui.TrustedNode;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.Arrays;

/**
 *
 * @author footman
 */
public class AudioCapture {
    private AudioFormat af;
    private DataLine.Info dli;
    private TargetDataLine tdl;

    int buf[];
    byte b[];
    private static final int LENGTH = 1024;
    /**
     * 打开音频目标数据行。从中读取音频数据格式为：采样率32kHz，每个样本16位，单声道，有符号的，little-endian。
     * @return 成功打开返回true，否则false。
     */
    public boolean open() {
    /**    af = new AudioFormat(8000.0F, 16, 1, true, false);
        dli = new DataLine.Info(TargetDataLine.class, af);
        try {
            tdl = (TargetDataLine) AudioSystem.getLine(dli);
            tdl.open(af, FFT.FFT_N << 1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }  **/
        buf = new int[LENGTH];
      //  b = new byte[LENGTH * 4];
        return true;
    }

    public void start() {
      //  tdl.start();
    }

    public void stop() {
       // tdl.stop();
        TrustedNode.AudioEnd();
    }

    public byte[] read() {

        TrustedNode.Record(buf, LENGTH);
        b = new byte[LENGTH * 4];
        for(int i=0; i<buf.length; i++) {
            b[i * 4] = (byte)(buf[i] >>> 24);
            b[i * 4 + 1] = (byte)((buf[i] >>> 16) & 0x000000ff);
            b[i * 4 + 2] = (byte)((buf[i] >>> 8) & 0x000000ff);
            b[i * 4 + 3] = (byte)((buf[i]) & 0x000000ff);
        }
        return b;
    }
}
