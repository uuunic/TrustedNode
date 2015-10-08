/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataHandler.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author footman
 */
public class AudioCapture {
    private AudioFormat af;
    private DataLine.Info dli;
    private TargetDataLine tdl;

    /**
     * 打开音频目标数据行。从中读取音频数据格式为：采样率32kHz，每个样本16位，单声道，有符号的，little-endian。
     * @return 成功打开返回true，否则false。
     */
    public boolean open() {
        af = new AudioFormat(8000.0F, 16, 1, true, false);
        dli = new DataLine.Info(TargetDataLine.class, af);
        try {
            tdl = (TargetDataLine) AudioSystem.getLine(dli);
            tdl.open(af, FFT.FFT_N << 1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void close() {
        tdl.close();
    }

    public void start() {
        tdl.start();
    }

    public void stop() {
        tdl.stop();
    }

    public int read(byte[] b, int len) {
        return tdl.read(b, 0, len);
    }
}
