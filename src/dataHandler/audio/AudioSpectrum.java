/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataHandler.audio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author footman
 */
public class AudioSpectrum extends JComponent{
    private static final long serialVersionUID = 1L;
    private static final int maxColums = 128;
    private static final int Y0 = 1 << ((FFT.FFT_N_LOG + 3) << 1);
    private static final double logY0 = Math.log10(Y0); //lg((8*FFT_N)^2)
    private FFT fft = new FFT();
    private int band;
    private int width, height;
    private int[] xplot, lastPeak, lastY;
    private int deltax;
    private long lastTimeMillis;
    private BufferedImage spectrumImage, barImage;
    private Graphics spectrumGraphics;

    public AudioSpectrum() {
        band = 64;      //64段
        width = 383;    //频谱窗口 383x124
        height = 124;
        lastTimeMillis = System.currentTimeMillis();
        xplot = new int[maxColums + 1];
        lastPeak = new int[maxColums];
        lastY = new int[maxColums];
        spectrumImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        spectrumGraphics = spectrumImage.getGraphics();
        setPreferredSize(new Dimension(width, height));
        setPlot();
        barImage = new BufferedImage(deltax - 1, height, BufferedImage.TYPE_3BYTE_BGR);

        setColor(0x7f7f7f, 0xff0000, 0xffff00, 0x7f7fff);
    }

    public void setColor(int rgbPeak, int rgbTop, int rgbMid, int rgbBot) {
        Color crPeak = new Color(rgbPeak);
        spectrumGraphics.setColor(crPeak);

        spectrumGraphics.setColor(Color.gray);
        Graphics2D g = (Graphics2D)barImage.getGraphics();
        Color crTop = new Color(rgbTop);
        Color crMid = new Color(rgbMid);
        Color crBot = new Color(rgbBot);
        GradientPaint gp1 = new GradientPaint(0, 0, crTop,deltax - 1,height/2,crMid);
        g.setPaint(gp1);
        g.fillRect(0, 0, deltax - 1, height/2);
        GradientPaint gp2 = new GradientPaint(0, height/2, crMid,deltax - 1,height,crBot);
        g.setPaint(gp2);
        g.fillRect(0, height/2, deltax - 1, height);
        gp1 = gp2 = null;
        crPeak = crTop = crMid = crBot = null;
    }

    private void setPlot() {
        deltax = (width - band + 1) / band + 1;

        // 0-16kHz分划为band个频段，各频段宽度非线性划分。
        for (int i = 0; i <= band; i++) {
            xplot[i] = 0;
            xplot[i] = (int) (0.5 + Math.pow(FFT.FFT_N >> 1, (double) i   / band));
            if (i > 0 && xplot[i] <= xplot[i - 1])
                xplot[i] = xplot[i - 1] + 1;
        }
    }

    /**
     * 绘制"频率-幅值"直方图并显示到屏幕。
     * @param amp amp[0..FFT.FFT_N/2-1]为频谱"幅值"(用复数模的平方)。
     */
    private void drawHistogram(float[] amp) {
        spectrumGraphics.clearRect(0, 0, width, height);

        long t = System.currentTimeMillis();
        int speed = (int)(t - lastTimeMillis) / 30; //峰值下落速度
        lastTimeMillis = t;

        int i = 0, x = 0, y, xi, peaki, w = deltax - 1;
        float maxAmp;
        for (; i != band; i++, x += deltax) {
            // 查找当前频段的最大"幅值"
            maxAmp = 0; xi = xplot[i]; y = xplot[i + 1];
            for (; xi < y; xi++) {
                if (amp[xi] > maxAmp)
                    maxAmp = amp[xi];
            }

            /*
             * maxAmp转换为用对数表示的"分贝数"y:
             * y = (int) Math.sqrt(maxAmp);
             * y /= FFT.FFT_N; //幅值
             * y /= 8;  //调整
             * if(y > 0) y = (int)(Math.log10(y) * 20 * 2);
             *
             * 为了突出幅值y显示时强弱的"对比度"，计算时作了调整。未作等响度修正。
             */
            y = (maxAmp > Y0) ? (int) ((Math.log10(maxAmp) - logY0) * 20) : 0;

            // 使幅值匀速度下落
            lastY[i] -= speed << 2;
            if(y < lastY[i]) {
                y = lastY[i];
                if(y < 0) y = 0;
            }
            lastY[i] = y;

            if(y >= lastPeak[i]) {
                lastPeak[i] = y;
            } else {
                // 使峰值匀速度下落
                peaki = lastPeak[i] - speed;
                if(peaki < 0)
                    peaki = 0;
                lastPeak[i] = peaki;
                peaki = height - peaki;
                spectrumGraphics.drawLine(x, peaki, x + w - 1, peaki);
            }

            // 画当前频段的直方图
            y = height - y;
            spectrumGraphics.drawImage(barImage, x, y, x+w, height, 0, y, w, height, null);
        }

        // 刷新到屏幕
        repaint(0, 0, width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(spectrumImage, 0, 0, null);
    }

    public synchronized void putDataAndDraw(byte[] data) {
        byte[] b;
        float realIO[] = new float[FFT.FFT_N];
        int i, j;
        b = data;
         //wi.getWave264(b, FFT.FFT_N << 1);//debug
        for (i = j = 0; i != FFT.FFT_N; i++, j += 2)
            realIO[i] = (b[j + 1] << 8) | (b[j] & 0xff); //signed short
                // 时域PCM数据变换到频域,取回频域幅值
        fft.calculate(realIO);
                // 绘制
        drawHistogram(realIO);
    }
}
