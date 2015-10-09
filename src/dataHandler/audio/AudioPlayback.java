/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandler.audio;

import ui.TrustedNode;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayback {

    SourceDataLine line;
    AudioFormat af;
    DataLine.Info info;
    private static final int LENGTH = 1024;
    int[] buf;
    /**
     * 打开音频目标数据行。从中读取音频数据格式为：采样率32kHz，每个样本16位，单声道，有符号的，little-endian。
     * @return 成功打开返回true，否则false。
     */
    public boolean open() {
     /**   System.out.println(FFT.FFT_N << 1);
        af = new AudioFormat(8000, 16, 1, true, false);
        info = new DataLine.Info(SourceDataLine.class, af);
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(af, FFT.FFT_N << 1);
        } catch (Exception e) {
            return false;
        }  **/
        buf = new int[LENGTH];
        return true;
    }

    public void stop() {
    }

    public void write(byte[] b) {

        for(int i=0; i<LENGTH; i++) {
//            buf[i] = (((int)(b[i * 4])) << 24) | (((int)(b[i * 4 + 1])) << 16) | (((int)(b[i * 4 + 2])) << 8) | ((int)b[i * 4 + 3]);
            buf[i] = (int) ((b[i * 4 + 3] & 0xFF)
                    | ((b[i * 4 + 2] & 0xFF)<<8)
                    | ((b[i * 4 + 1] & 0xFF)<<16)
                    | ((b[i * 4] & 0xFF)<<24));
        }

        TrustedNode.Player(buf, buf.length);
    }

}

