/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;


import client.Node;
import client.NodePool;
import dataHandler.audio.AudioHandler;
import dataHandler.text.MessageHandler;
import encryption.AESByte;
import ui.Handler;
import ui.TrustedNode;
import util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 *
 * @author footman
 */
public class ReceivePacketThread extends Thread {
	private static final String THREAD_NAME = "ReceivePacket";
	private static final String TAG = "ReceivePacketThread:";
	private static final int MAX_DATALENGTH = 1000;
	
	private PacketThreadManager parent;
	
	private NodePool nodePool;
	private final Handler handler;
	private DatagramSocket datagramSocket;
	private volatile boolean shutdownRequested = false;

	public ReceivePacketThread(PacketThreadManager packetThreadManager, DatagramSocket socket, NodePool nodePool, Handler handler){
		super(THREAD_NAME);
		this.parent = packetThreadManager;
		this.datagramSocket = socket;
		this.nodePool = nodePool;
		this.handler = handler;
	}
	
	public void shutdownRequest(){
		this.shutdownRequested = true;
	}
	
	public boolean isShutdownRequested(){
		return this.shutdownRequested;
	}
	
	public void run(){
		while(!this.shutdownRequested){
			receivePacket();
		}
	}
	/**若IP地址为本地Client地址，则返回原地指；</br>
	 * 若IP地址为另一服务器上Client地址，则返回另一个Server的地址IP
	 * **/
	private String getDestIP(String IP){
		return nodePool.getNode(IP).getIP();
	}
	
	private void receivePacket(){
		
		try {
			DatagramPacket packet = new DatagramPacket(new byte[MAX_DATALENGTH], MAX_DATALENGTH);
			datagramSocket.receive(packet);
			String packetFromIP = packet.getAddress().getHostAddress();
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
			ObjectInputStream ois = new ObjectInputStream(bis);
			DatagramPackage datagramPackage = (DatagramPackage)ois.readObject();
			ois.close();
			bis.close();
			String sourceIP = datagramPackage.getSourceIP();
			String destIP = datagramPackage.getDestIP();
			Log.i(TAG, "received a datagram packet from " + packetFromIP);

			if (destIP.equals(PacketThreadManager.getLocalIP())){
				//是发送给自己的数据包
				System.out.println(TAG + "收到自己的数据包");
				switch(datagramPackage.getKeyWord()){
				case TEXT:
					ByteArrayInputStream textBis = new ByteArrayInputStream(datagramPackage.getByteData());
					ObjectInputStream textOis = new ObjectInputStream(textBis);
					MessagePackage massagePackage = ((MessagePackage)textOis.readObject());
					byte[] data = massagePackage.getData();
					byte[] sourcekey = nodePool.getAESKey(sourceIP);
					byte[] textdata = AESByte.decryptByte(data, sourcekey);
					String text = new String(textdata, "GBK");
                    Node sourceClient = nodePool.getNode(sourceIP);
                    Node destClient = nodePool.getNode(destIP);
					MessageHandler messageHandler = new MessageHandler(
							text, 
							sourceClient.getName(),
                                                        sourceClient.getIP(),
							destClient.getName(),
                                                        destClient.getIP());
					handler.obtainMessage(TrustedNode.HANDLER_ADD_TEXT, messageHandler).sendToTarget();
					textOis.close();
					textBis.close();
                    break;
				case REQAUDIO:
					Node reqSourceClient = nodePool.getNode(sourceIP);
					Node reqDestClient = nodePool.getNode(destIP);
					AudioHandler audioHandlerREQRECE = new AudioHandler(DatagramPackage.KeyWord.REQAUDIO,
							reqSourceClient.getName(), reqSourceClient.getIP(),
							reqDestClient.getName(), reqDestClient.getIP());
					handler.obtainMessage(TrustedNode.HANDLER_REQ_AUDIO_RECE, audioHandlerREQRECE).sendToTarget();
					break;
				case ACCAUDIO:
					Node accSourceClient = nodePool.getNode(sourceIP);
					Node accDestClient = nodePool.getNode(destIP);
					parent.sendAudioPacketThread = new SendAudioPacketThread(parent.audioSocket, nodePool, parent.senAudioDataBuffer, handler);
					parent.sendAudioPacketThread.setAudioClient(accSourceClient);
					parent.sendAudioPacketThread.start();
					parent.audioPlaybackThread.startPlayback();
					AudioHandler audioHandlerACCRECE = new AudioHandler(DatagramPackage.KeyWord.ACCAUDIO, 
							accSourceClient.getName(), accSourceClient.getIP(),
							accDestClient.getName(), accDestClient.getIP());
					handler.obtainMessage(TrustedNode.HANDLER_ACC_AUDIO_RECE, audioHandlerACCRECE).sendToTarget();
					break;
				case REFAUDIO:
					Node refSourceClient = nodePool.getNode(sourceIP);
					Node refDestClient = nodePool.getNode(destIP);
					AudioHandler audioHandlerREFRECE = new AudioHandler(DatagramPackage.KeyWord.REFAUDIO, 
							refSourceClient.getName(), refSourceClient.getIP(),
							refDestClient.getName(), refDestClient.getIP());
					handler.obtainMessage(TrustedNode.HANDLER_REF_AUDIO_RECE, audioHandlerREFRECE).sendToTarget();
					break;
				case ENDAUDIO:
					Node endSourceClient = nodePool.getNode(sourceIP);
					Node endDestClient = nodePool.getNode(destIP);
					parent.sendAudioPacketThread.shutdownRequest();
					parent.audioPlaybackThread.stopPlayback();
					AudioHandler audioHandlerENDRECE = new AudioHandler(DatagramPackage.KeyWord.ENDAUDIO, 
							endSourceClient.getName(), endSourceClient.getIP(),
							endDestClient.getName(), endDestClient.getIP());
					handler.obtainMessage(TrustedNode.HANDLER_END_AUDIO_RECE, audioHandlerENDRECE).sendToTarget();
					break;
				default:
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
