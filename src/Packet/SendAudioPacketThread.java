/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;

import client.Node;
import client.NodePool;
import dataHandler.audio.AudioCapture;
import dataHandler.audio.AudioDataBuffer;
import encryption.AESByte;
import ui.Handler;
import ui.TrustedNode;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author footman
 */
public class SendAudioPacketThread extends Thread {
	private static final String THREAD_NAME = "SendAudio";
	private static final String TAG = "SendAudioThread";
	
	private NodePool nodePool;
	private Node audioNode;
	private final Handler handler;
	private DatagramSocket audioSocket;
	private volatile boolean shutdownRequested = false;
	private AudioDataBuffer audioDataBuffer;
	private byte[] sendData;
	
	/**
	 * 创建一个新的AudioPacketThread
	 * @param socket AudioDatagramSocket
	 * @param nodePool
	 * @param handler
	 */
	public SendAudioPacketThread(DatagramSocket socket, NodePool nodePool, AudioDataBuffer audioDataBuffer, Handler handler){
		super(THREAD_NAME);
		this.audioSocket = socket;
		this.nodePool = nodePool;
		this.handler = handler;
		this.audioDataBuffer = audioDataBuffer;
	}
	
	public void setAudioClient(Node node){
		if (node != null){
			audioNode = node;
		}
	}
	
	public Node getAudioClient(){
		return audioNode;
	}
	
	public void shutdownRequest(){
		this.shutdownRequested = true;
		interrupt();
	}
	
	private void stopRequest(){
		this.shutdownRequested = true;
		handler.obtainMessage(TrustedNode.HANDLER_ERROR_AUDIO_ONLINE).sendToTarget();
		interrupt();
	}
	
	public boolean isShutdownRequested(){
		return this.shutdownRequested;
	}
	
	public void run(){
		shutdownRequested = false;
		try{
				while(!this.shutdownRequested){
				sendAudioPacket();
			}
		}catch (InterruptedException e){
			
		}
	}
	
	private void sendAudioPacket() throws InterruptedException{
		byte[] data = audioDataBuffer.getAudioData();
		if (!audioNode.isOnline()){
			stopRequest();
			throw new InterruptedException();
		}
		try{
			DatagramPackage datagramPackage = new DatagramPackage(DatagramPackage.KeyWord.AUDIO, 
					PacketThreadManager.getLocalIP(), audioNode.getIP());
			byte[] encryptdata = AESByte.encryptByte(data, nodePool.getAESKey(audioNode.getIP()));
			datagramPackage.addByteData(encryptdata);
			sendData = datagramPackage.toByteArray();
				//如果是邻居节点，直接发送就可以了
			SocketAddress targetAddress = new InetSocketAddress(audioNode.getIP(), PacketThreadManager.AUDIO_RECEIVE_PORT);
			DatagramPacket audioPacket = new DatagramPacket(sendData, sendData.length, targetAddress);
			audioSocket.send(audioPacket);
			handler.obtainMessage(TrustedNode.HANDLER_SEND_AUDIO_PACKAGE, data).sendToTarget();
		} catch (SocketException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
