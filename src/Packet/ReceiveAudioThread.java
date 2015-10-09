/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;


import client.NodePool;
import dataHandler.audio.AudioDataBuffer;
import dataHandler.audio.AudioPlayback;
import encryption.AESByte;
import ui.Handler;
import ui.TrustedNode;
import util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 *
 * @author footman
 */
public class ReceiveAudioThread extends Thread {
	private static final String THREAD_NAME = "ReceiveAudio";
	private static final String TAG = "ReceiveAudioThread";
	private static final int MAX_DATALENGTH = 8421;//长度为sendAudioPacketThread.Max_dataLength+(1421-1024)
	
	private NodePool nodePool;
	private final Handler handler;
	private DatagramSocket audioSocket;
	private volatile boolean shutdownRequested = false;
	private AudioDataBuffer audioDataBuffer;
	
	/**
	 * 创建一个新的ReceiveAudioThread
	 * @param socket
	 * @param nodePool
	 * @param handler
	 */
	public ReceiveAudioThread(DatagramSocket socket, NodePool nodePool, AudioDataBuffer audioDataBuffer, Handler handler){
		super(THREAD_NAME);
		this.audioSocket = socket;
		this.nodePool = nodePool;
		this.handler = handler;
		this.audioDataBuffer = audioDataBuffer;
	}
	
	public void shutdownRequest(){
		this.shutdownRequested = true;
	}

	public boolean isShutdownRequested(){
		return this.shutdownRequested;
	}
	
	public void run(){
		while(!this.shutdownRequested){
			receiveAudioPacket();
		}
	}
	
	private void receiveAudioPacket(){
		try {
			long time1 = System.currentTimeMillis();
			DatagramPacket packet = new DatagramPacket(new byte[MAX_DATALENGTH], MAX_DATALENGTH);
			audioSocket.receive(packet);
			String packetFromIP = packet.getAddress().getHostAddress();
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			DatagramPackage datagramPackage = (DatagramPackage)ois.readObject();
			ois.close();
			bis.close();
			String sourceIP = datagramPackage.getSourceIP();
			String destIP = datagramPackage.getDestIP();
			Log.i(TAG, "发送 IP为：" + packetFromIP);

			if(destIP.equals(PacketThreadManager.getLocalIP())){
				Log.i(TAG, "：是给自己的audio包");
				//是发送给自己的数据包，播放出来
				switch(datagramPackage.getKeyWord()){
					case AUDIO:
						byte[] encryptData = datagramPackage.getByteData();
						byte[] audioData = AESByte.decryptByte(encryptData, nodePool.getAESKey(sourceIP));
						this.audioDataBuffer.putAudioData(audioData);
						handler.obtainMessage(TrustedNode.HANDLER_GET_AUDIO_PACKAGE, encryptData).sendToTarget();
						break;
					default:
						break;
				}
            } else {
				Log.e(TAG, "Fatal error, this package is not for me. destIP : " + destIP);
			}
			long time2 = System.currentTimeMillis();
			System.out.println("Audio receive time is : " + (time2 - time1));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
