package dataHandler.audio;

import ui.TrustedNode;

/**
 * Created by Weiran Liu on 2015/10/8.
 */
public class AudioCaptureThread extends Thread {
    private static final String THREAD_NAME = "AudioCaptureThread";
    private static final String TAG = "AudioCaptureThread";

    private volatile boolean shutdownRequested = false;
    private AudioCapture audioCapture;
    private AudioDataBuffer audioDataBuffer;
    private byte[] data;

    public AudioCaptureThread(AudioDataBuffer audioDataBuffer) {
        this.audioCapture = new AudioCapture();
        audioCapture.open();
        this.audioDataBuffer = audioDataBuffer;
        this.data = new byte[audioDataBuffer.getAudioDataLength()];
    }

    public boolean isShutdownRequested(){
        return this.shutdownRequested;
    }

    public void shutdownRequest(){
        this.shutdownRequested = true;
        audioCapture.stop();
        audioCapture.close();
        audioCapture = null;
        interrupt();
    }

    public void run(){
        audioCapture.start();
        shutdownRequested = false;
        try{
            while(!this.shutdownRequested){
                int length = audioCapture.read(data, data.length);
                this.audioDataBuffer.putAudioData(data);
            }
        }catch (InterruptedException e){

        }
    }
}
