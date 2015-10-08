package dataHandler.audio;

/**
 * Created by Weiran Liu on 2015/10/8.
 */
public class AudioPlaybackThread extends Thread {
    private static final String THREAD_NAME = "AudioPlaybackThread";
    private static final String TAG = "AudioPlaybackThread";

    private volatile boolean shutdownRequested = false;
    private AudioPlayback audioPlayback;
    private boolean isPlaybackStart;
    private AudioDataBuffer audioDataBuffer;

    public AudioPlaybackThread(AudioDataBuffer audioDataBuffer) {
        this.audioDataBuffer = audioDataBuffer;
        audioPlayback = new AudioPlayback();
        audioPlayback.open();
        isPlaybackStart = false;
    }

    public void shutdownRequest(){
        this.shutdownRequested = true;
    }

    public boolean isShutdownRequested(){
        return this.shutdownRequested;
    }

    public void startPlayback(){
        audioPlayback.start();
        isPlaybackStart = true;
    }

    public void stopPlayback(){
        audioPlayback.stop();
        isPlaybackStart = false;
    }

    public void run(){
        while(!this.shutdownRequested){
            try {
                byte[] audioData = this.audioDataBuffer.getAudioData();
                audioPlayback.write(audioData, audioData.length);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (isPlaybackStart){
            audioPlayback.stop();
        }
    }
}
