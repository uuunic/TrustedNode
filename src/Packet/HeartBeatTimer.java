/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;

import client.Node;
import client.NodePool;
import ui.Handler;
import ui.TrustedNode;

import java.io.IOException;
import java.net.*;
import java.util.*;


/**
 *
 * @author footman
 */
public class HeartBeatTimer {
	private static final String TAG = "HeartBeatTimer";
	private static final long TIME_OUT = 3000L;
	private DatagramSocket heartBeatSocket;
	private NodePool nodePool;
	private Handler handler;
	private final Timer timer;
	private boolean isRunning;

	public HeartBeatTimer(DatagramSocket socket, NodePool nodePool, Handler handler){
		this.heartBeatSocket = socket;
		this.nodePool = nodePool;
		this.handler = handler;
		this.timer = new Timer();
		this.isRunning = false;
	}
	
	public void startHeartBeat(){
		this.timer.schedule(new HeartBeatTimerTask(), 100L, 1000L);
		isRunning = true;
	}
	
	public void stopHeartBeat(){
		this.timer.cancel();
		isRunning = false;
	}
	
	public boolean isRunning(){
		return this.isRunning;
	}
	
	
	public class HeartBeatTimerTask extends TimerTask{
		
		@Override
		public void run() {
			long nowTime = System.currentTimeMillis();
			Iterator it = nodePool.iterator();

			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String IP = (String)entry.getKey();
				Node node = (Node)entry.getValue();
				long lastTime = node.getLastHeartBeatTime();
				//检查是否超时，超时则设置为不在线
				if((node.isOnline() && nowTime - lastTime > TIME_OUT) ) {
					node.setIsOnline(false);
					handler.obtainMessage(TrustedNode.HANDLER_UPDATE_CLIENT_STATE, node).sendToTarget();
				}
				//检查完是否在线之后，再想其他节点发送心跳包
				SocketAddress destAddress = new InetSocketAddress(node.getIP(), PacketThreadManager.HEART_BEAT_PORT);
				try{
					DatagramPackage datagramPackage = new DatagramPackage(DatagramPackage.KeyWord.HEART_BEAT, nodePool.getLocalNode().getIP(), node.getIP());
					byte[] data = datagramPackage.toByteArray();
					DatagramPacket outputPacket = new DatagramPacket(data, data.length, destAddress);
					heartBeatSocket.send(outputPacket);
				} catch (SocketException e){
					System.out.println(TAG + "数据包发送失败...目标：" + destAddress);
					e.printStackTrace();
				} catch (IOException e){
					System.out.println(TAG + "数据包创建失败...目标：" + destAddress);
					e.printStackTrace();
				}

			}
		}

	}
}

