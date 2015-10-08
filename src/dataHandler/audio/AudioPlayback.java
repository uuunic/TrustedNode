/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandler.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayback {

    SourceDataLine line;
    AudioFormat af;
    DataLine.Info info;

    /**
     * 打开音频目标数据行。从中读取音频数据格式为：采样率32kHz，每个样本16位，单声道，有符号的，little-endian。
     * @return 成功打开返回true，否则false。
     */
    public boolean open() {
        System.out.println(FFT.FFT_N << 1);
        af = new AudioFormat(8000, 16, 1, true, false);
        info = new DataLine.Info(SourceDataLine.class, af);
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(af, FFT.FFT_N << 1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void close() {
        line.close();
    }

    public void start() {
        line.start();
    }

    public void stop() {
        line.stop();

    }

    public int write(byte[] b, int len) {
        return line.write(b, 0, len);
    }
}

