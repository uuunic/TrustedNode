/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;

import client.Node;
import client.NodePool;
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
public class ReceiveHeartBeatThread extends Thread {
	private static final String THREAD_NAME = "HeartBeatThread";
	private static final String TAG = "ReceiveHeartBeatThread";
	private static final int MAX_DATALENGTH = 1024;
	
	private Handler handler;
	private DatagramSocket heartBeatSocket;
	private volatile boolean shutdownRequested = false;
	private NodePool nodePool;
	
	public ReceiveHeartBeatThread(DatagramSocket heartBeatSocket, NodePool clientPool, Handler handler){
		super(THREAD_NAME);
		this.heartBeatSocket = heartBeatSocket;
		this.nodePool = clientPool;
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
			receiveHeartBeatPacket();
		}
	}
	
	private void receiveHeartBeatPacket(){
		try {
			DatagramPacket packet = new DatagramPacket(new byte[MAX_DATALENGTH], MAX_DATALENGTH);
			heartBeatSocket.receive(packet);
			String packetFromIP = packet.getAddress().getHostAddress();
			
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
			ObjectInputStream ois = new ObjectInputStream(bis);
			DatagramPackage datagramPackage = (DatagramPackage)ois.readObject();
			ois.close();
			bis.close();
			switch(datagramPackage.getKeyWord()){
				case HEART_BEAT:
					Node node = nodePool.getNode(packetFromIP);
					if (node == null){
						Log.e(TAG, "the node is not exist! IP :" + packetFromIP);
						return;
						
					}else if (!node.isOnline()){
						node.setIsOnline(true);
						
						nodePool.updateLastHeartBeatTime(packetFromIP);
						handler.obtainMessage(TrustedNode.HANDLER_UPDATE_CLIENT_STATE, node).sendToTarget();
						Log.v(TAG , packetFromIP + " is set from offline -> online");
					}else{
						nodePool.updateLastHeartBeatTime(packetFromIP);
					}
					
					break;
				default:
						Log.e(TAG, "get the heartBeat from default!!!! IP : " + packetFromIP);

			}
			
        } catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
