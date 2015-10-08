package Packet;

import accessPackage.RSAPackage0;
import accessPackage.RSAPackage1;
import accessPackage.RSAPackage2;
import accessPackage.RSAPackage3;
import client.Node;
import client.NodePool;
import ui.Handler;
import ui.TrustedNode;
import util.Log;
import util.Tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.interfaces.RSAPublicKey;

public class ServerReceiveAccessPacketThread extends Thread{
	private static final String THREAD_NAME = "ReceiveAccessPacket";
	private static final String TAG = "ServerReceiveAccessPacketThread: ";
	private static final int MAX_DATALENGTH = 1500;
	
	private PacketThreadManager parent;
	private NodePool nodePool;
	private final Handler handler;
	private DatagramSocket accessSocket;
	private volatile boolean shutdownRequested = false;
	
	public ServerReceiveAccessPacketThread(PacketThreadManager packetThreadManager, DatagramSocket socket, NodePool nodePool, Handler handler){
		super(THREAD_NAME);
		this.parent = packetThreadManager;
		this.accessSocket = socket;
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
			receiveAccessPacket();
		}
	}
	
	private void receiveAccessPacket(){
		try {
			DatagramPacket packet = new DatagramPacket(new byte[MAX_DATALENGTH], MAX_DATALENGTH);
			accessSocket.receive(packet);
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
			ObjectInputStream ois = new ObjectInputStream(bis);
			DatagramPackage datagramPackage = (DatagramPackage)ois.readObject();
			ois.close();
			bis.close();
			String sourceIP = datagramPackage.getSourceIP();
			String destIP = datagramPackage.getDestIP();
			

			//若pool中不存在待接入的Client，那么就继续
			if(nodePool.getNode(sourceIP) == null){
				Log.e(TAG, " 源客户端不存在！");
			} else {
				//记得更新在线时间；
				nodePool.updateLastHeartBeatTime(sourceIP);
			}
			
			Node sourceNode = nodePool.getNode(sourceIP);
			//AccessPacket一定是发送给自己的数据包
			ByteArrayInputStream inputStream = new ByteArrayInputStream(datagramPackage.getByteData());
			ObjectInputStream rsaAccess = new ObjectInputStream(inputStream);
			switch(datagramPackage.getKeyWord()){
				case RSA_PACKAGE0:
					Log.i(TAG, "收到RSAPackage0, " + sourceNode.getName() + "准备接入！");
					RSAPackage0 rsa0 = (RSAPackage0)rsaAccess.readObject();
					handler.obtainMessage(TrustedNode.HANDLER_RECEIVE_RSA_0_PACKAGE, rsa0).sendToTarget();
					RSAPackage1 rsa1 = nodePool.requestForRSAPublicKey(rsa0);
					DatagramPackage package1 = new DatagramPackage(DatagramPackage.KeyWord.RSA_PACKAGE1, destIP, sourceIP);
					package1.addByteData(rsa1.toByteArray());
					new ServerSendAccessPacketRunnable(accessSocket, sourceIP, package1.toByteArray()).run();
					handler.obtainMessage(TrustedNode.HANDLER_SEND_RSA_1_PACKAGE, rsa1).sendToTarget();

					Log.i(TAG, "RSAPackage1已发送，等待" + sourceNode.getName() + "的下一步行动");
					break;


				case RSA_PACKAGE1:
					Log.i(TAG, "收到RSAPackage1.");
					RSAPackage1 rsa1_received = (RSAPackage1)rsaAccess.readObject();
					handler.obtainMessage(TrustedNode.HANDLER_RECEIVE_RSA_1_PACKAGE, rsa1_received).sendToTarget();
					RSAPackage2 p2_send = nodePool.requestForServerAESKey(rsa1_received);
					DatagramPackage package2 = new DatagramPackage(DatagramPackage.KeyWord.RSA_PACKAGE2, destIP, sourceIP);
					package2.addByteData(p2_send.toByteArray());
					new ServerSendAccessPacketRunnable(accessSocket, sourceIP, package2.toByteArray()).run();

					handler.obtainMessage(TrustedNode.HANDLER_SEND_RSA_2_PACKAGE, p2_send).sendToTarget();
					Log.i(TAG, "RSAPackage2已发送，等待" + sourceNode.getName() + "的下一步行动");
					break;

				case RSA_PACKAGE2:
					Log.i(TAG, "收到RSAPackage2.");
					RSAPackage2 rsa2_received = (RSAPackage2)rsaAccess.readObject();
					handler.obtainMessage(TrustedNode.HANDLER_RECEIVE_RSA_2_PACKAGE, rsa2_received).sendToTarget();

					RSAPackage3 p3_send = nodePool.callbackToClient(rsa2_received);
					DatagramPackage package3 = new DatagramPackage(DatagramPackage.KeyWord.RSA_PACKAGE3, destIP, sourceIP);
					package3.addByteData(p3_send.toByteArray());
					new ServerSendAccessPacketRunnable(accessSocket, sourceIP, package3.toByteArray()).run();

					handler.obtainMessage(TrustedNode.HANDLER_SEND_RSA_3_PACKAGE, p3_send).sendToTarget();
					Log.i(TAG, "RSAPackage3已发送，等待" + sourceNode.getName() + "的下一步行动");
					nodePool.setNodeIsValid(sourceIP, true);
					handler.obtainMessage(TrustedNode.HANDLER_UPDATE_CLIENT_STATE, nodePool.getNode(sourceIP)).sendToTarget();

					break;

				case RSA_PACKAGE3:
					Log.i(TAG, "收到RSAPackage3.");
					RSAPackage3 rsa3_received = (RSAPackage3)rsaAccess.readObject();
					handler.obtainMessage(TrustedNode.HANDLER_RECEIVE_RSA_3_PACKAGE, rsa3_received).sendToTarget();

					boolean isValid = nodePool.clientAuthEnd(rsa3_received);
					nodePool.setNodeIsValid(sourceIP, isValid);
					handler.obtainMessage(TrustedNode.HANDLER_UPDATE_CLIENT_STATE, nodePool.getNode(sourceIP)).sendToTarget();
					break;

				default:
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
