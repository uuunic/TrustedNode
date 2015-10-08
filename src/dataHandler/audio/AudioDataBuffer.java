package dataHandler.audio;

import java.util.Arrays;

/**
 * Created by Weiran Liu on 2015/10/8.
 */
public class AudioDataBuffer {
    private final byte[][] audioBuffer;
    //next index to put audio data
    private int dataLength;

    private int tail;
    //next index to take audio data
    private int head;
    //number of audio data
    private int count;

    public AudioDataBuffer(int bufferLength, int dataLength) {
        this.audioBuffer = new byte[bufferLength][dataLength];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
        this.dataLength = dataLength;
    }

    /**
     * put audio data into the audioBuffer
     * @param data audio data to be put
     * @throws InterruptedException
     */
    public synchronized void putAudioData(byte[] data) throws InterruptedException {
        while (count >= audioBuffer.length) {
            System.out.println("AudioDataBuffer: audio data buffer is full");
            wait();
        }
        audioBuffer[tail] = Arrays.copyOf(data, data.length);
        tail = (tail + 1) % audioBuffer.length;
        count++;
        notifyAll();
    }

    /**
     * get audio data
     * @return audio data
     * @throws InterruptedException
     */
    public synchronized byte[] getAudioData() throws InterruptedException {
        while (count <= 0) {
            wait();
        }
        byte[] audioData = Arrays.copyOf(audioBuffer[head], audioBuffer[head].length);
        head = (head + 1) % audioBuffer.length;
        count--;
        notifyAll();
        return audioData;
    }

    public int getAudioDataLength() {return this.dataLength; }

}
