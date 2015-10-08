/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;

import accessPackage.RSAPackage0;
import client.Node;
import client.NodePool;
import dataHandler.audio.AudioCaptureThread;
import dataHandler.audio.AudioDataBuffer;
import dataHandler.audio.AudioHandler;
import dataHandler.audio.AudioPlaybackThread;
import dataHandler.text.MessageHandler;
import encryption.AESByte;
import ui.Handler;
import ui.TrustedNode;
import util.ConfigHandler;
import util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author firefix
 */
public class PacketThreadManager {
	private static final String TAG = "PacketThreadManager";
	public static final int RECEIVE_PORT = 50001;
	public static final int AUDIO_RECEIVE_PORT = 51002;
	public static final int HEART_BEAT_PORT = 52003;
	public static final int TRUST_ACCESS_PORT = 53004;
	
	private static final int POOL_SIZE = 10;
	public static final String SERVER_NAME = "Server1";
	public static final String SERVER_IP = "192.168.1.111";
	public static final String ALICE_NAME = "Alice";
	public static final String ALICE_IP = "192.168.1.145";
	public static final String BOB_NAME = "Bob";
	public static final String BOB_IP = "192.168.1.147";
	public static final String MALLORY_NAME = "Mallory";
	public static final String MALLORY_IP = "192.168.1.4";
	
	
	public static final String OTHER_SERVER_NAME = "Server2";
	public static final String OTHER_SERVER_IP = "192.168.1.19";
	
	
	private Handler handler;
//	private NodeHeap clientHeap;
	private NodePool nodePool;
	
	private Node currentNode;
	
	private DatagramSocket datagramSocket;
	private ReceivePacketThread receivePacketThread;
	
	private DatagramSocket heartBeatSocket;
	private ReceiveHeartBeatThread receiveHeartBeatThread;
	private HeartBeatTimer heartBeatTimer;
	
	public DatagramSocket audioSocket;
	public ReceiveAudioThread receiveAudioThread;
	public SendAudioPacketThread sendAudioPacketThread;
	public AudioCaptureThread audioCaptureThread;
	public AudioDataBuffer senAudioDataBuffer;
	private AudioDataBuffer recAudioDataBuffer;
	public AudioPlaybackThread audioPlaybackThread;

	private DatagramSocket accessSocket;
	private ServerReceiveAccessPacketThread serverReceiveAccessPacketThread;
	
	public ExecutorService threadPool;

	
	public PacketThreadManager(Handler handler){
		this.handler = handler;
		threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		this.recAudioDataBuffer = new AudioDataBuffer(10, 1024);
		this.senAudioDataBuffer = new AudioDataBuffer(10, 1024);
	}
	
	public boolean setCurrentNodeByName(String name){
		Node node = nodePool.getNodeByName(name);
		if (node != null){
			currentNode = node;
			return true;
		}else {
			return false;
		}
	}

	public Node getCurrentNode(){
		return this.currentNode;
	}
	public NodePool getNodePool(){
        	return this.nodePool;
        }
	
	public Node initLocalNet(){
		String localIP = PacketThreadManager.getLocalIP();
		nodePool = new NodePool();
		if (localIP != null){
			Properties p = ConfigHandler.getProperties();
			String[] names = p.getProperty("name").trim().split(",");
			String[] IPs = p.getProperty("IP").trim().split(",");
			if(names == null || names.length == 0 || names.length != IPs.length) {
				Log.e(this.getClass().getName(), "IP & name amount is not match! Please check the config!");
			}else {
				for(int i=0; i<names.length; i++){
					Node node = new Node(IPs[i], names[i]);
					if(IPs[i].equals(PacketThreadManager.getLocalIP())){
						node.setIsOnline(true);
						node.setIsValid(true);
					}
					nodePool.addNode(IPs[i], node);

					handler.obtainMessage(TrustedNode.HANDLER_ADD_CLIENT, node).sendToTarget();

				}
			}
		}else{
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_WIFI).sendToTarget();
		}
		return nodePool.getNode(getLocalIP());
	}
	
	public boolean onCreatThread(){
		try {
			datagramSocket = new DatagramSocket(PacketThreadManager.RECEIVE_PORT);
			receivePacketThread = new ReceivePacketThread(this, datagramSocket, nodePool, handler);
			Log.i(TAG, "DatagramSocket创建成功！");

			heartBeatSocket = new DatagramSocket(PacketThreadManager.HEART_BEAT_PORT);
			receiveHeartBeatThread = new ReceiveHeartBeatThread(heartBeatSocket, nodePool, handler);
			heartBeatTimer = new HeartBeatTimer(heartBeatSocket, nodePool, handler);
			Log.i(TAG, "HeartBeatSocket创建成功！");
			
			audioSocket = new DatagramSocket(PacketThreadManager.AUDIO_RECEIVE_PORT);
			receiveAudioThread = new ReceiveAudioThread(audioSocket, nodePool, recAudioDataBuffer, handler);
			audioCaptureThread = new AudioCaptureThread(this.senAudioDataBuffer);
			this.audioPlaybackThread = new AudioPlaybackThread(recAudioDataBuffer);
			Log.i(TAG, "AudioHeartBeatSocket创建成功！");
			
			accessSocket = new DatagramSocket(PacketThreadManager.TRUST_ACCESS_PORT);
			serverReceiveAccessPacketThread = new ServerReceiveAccessPacketThread(this, accessSocket, nodePool, handler);
			if (TrustedNode.DEBUG) System.out.println(TAG + " " + "AccessSocket创建成功！");

			return true;
		} catch (SocketException e) {
			e.printStackTrace();
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_WIFI).sendToTarget();
			return false;
		}
		
	}
	
	public void onStartThread(){
		receivePacketThread.start();
		receiveHeartBeatThread.start();
		serverReceiveAccessPacketThread.start();
		heartBeatTimer.startHeartBeat();
		receiveAudioThread.start();
		this.audioPlaybackThread.start();
		this.audioCaptureThread.start();

		Node localNode = nodePool.getLocalNode();
		Iterator it = nodePool.iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			Node destNode = (Node)entry.getValue();
			String destIP = destNode.getIP();
			if(!destIP.equals(localNode.getIP())) {
				RSAPackage0 p0 = nodePool.clientStartAuth();
				handler.obtainMessage(TrustedNode.HANDLER_SEND_RSA_0_PACKAGE, p0).sendToTarget();
				DatagramPackage datagramPackage = new DatagramPackage(DatagramPackage.KeyWord.RSA_PACKAGE0, localNode.getIP(), destIP);
				datagramPackage.addByteData(p0.toByteArray());
				byte[] data_dp = datagramPackage.toByteArray();
				threadPool.execute(new ServerSendAccessPacketRunnable(accessSocket, destIP, data_dp));
			}
		}
	}
	
	public void onStopThread(){
		if (!receivePacketThread.isShutdownRequested()){
			receivePacketThread.shutdownRequest();
		}
		receivePacketThread = null;
		if (!receiveHeartBeatThread.isShutdownRequested()){
			receiveHeartBeatThread.shutdownRequest();
		}
		receiveHeartBeatThread = null;
		if (heartBeatTimer.isRunning()){
			heartBeatTimer.stopHeartBeat();
		}
		heartBeatTimer = null;
		if (!receiveAudioThread.isShutdownRequested()){
			receiveAudioThread.shutdownRequest();
		}
		receiveAudioThread = null;
		if (sendAudioPacketThread != null && !sendAudioPacketThread.isShutdownRequested()){
			sendAudioPacketThread.shutdownRequest();
		}
		sendAudioPacketThread = null;
		if (this.audioPlaybackThread != null && !audioPlaybackThread.isShutdownRequested()) {
			audioPlaybackThread.shutdownRequest();
		}
		audioPlaybackThread = null;
		if (this.audioCaptureThread != null && !audioCaptureThread.isShutdownRequested()) {
			audioCaptureThread.shutdownRequest();
		}
		audioCaptureThread = null;
	}
	
	public boolean sendTextMessage(String text){
		if (currentNode == null){
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_NULL_CURRENT_CLIENT).sendToTarget();
			return false;
		}
//		else if (currentNode  == this.nodePool.getLocalNode()){
//			handler.obtainMessage(TrustedNode.HANDLER_ERROR_NULL_CURRENT_CLIENT).sendToTarget();
//			return false;
//		}
		else if (!currentNode.isOnline() || !currentNode.isValid()){
			System.out.println(TAG + "： currentNode" + currentNode.getIP());
			Log.v(TAG, "send text failed, currentNode is not illegal");
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_CURRENT_CLIENT_OFFLINE).sendToTarget();
			return false;
		
		} else {
			byte[] data;
			try{
				data = text.getBytes("utf-8");
				byte[] encryptData = AESByte.encryptByte(data, nodePool.getAESKey(currentNode.getIP()));
				MessagePackage messagePackage = new MessagePackage(encryptData);
				DatagramPackage datagramPackage = new DatagramPackage(DatagramPackage.KeyWord.TEXT,
						PacketThreadManager.getLocalIP(),
						currentNode.getIP());
				datagramPackage.addByteData(messagePackage.toByteArray());
				threadPool.execute(new SendPacketRunnable(datagramSocket, currentNode.getIP(), datagramPackage.toByteArray()));
				MessageHandler messageHandler = new MessageHandler(text, nodePool.getLocalNode().getName(), nodePool.getLocalNode().getIP(),
                                        currentNode.getName(), currentNode.getIP());
				handler.obtainMessage(TrustedNode.HANDLER_ADD_TEXT,
						messageHandler).sendToTarget();
				return true;
			}catch (UnsupportedEncodingException ex){
				ex.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * 本地节点向currentNode发送AudioInfo
	 */
	public void reqAudio(){
		if (currentNode == null){
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_NULL_CURRENT_CLIENT).sendToTarget();
			return;
		}else if (currentNode == this.nodePool.getLocalNode()){
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_NULL_CURRENT_CLIENT).sendToTarget();
			return;
		}else if (!currentNode.isOnline() || !currentNode.isValid()){
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_CURRENT_CLIENT_OFFLINE).sendToTarget();
			return;
		}
		AudioHandler audioHandler = new AudioHandler(DatagramPackage.KeyWord.REQAUDIO,
				nodePool.getLocalNode().getName(), nodePool.getLocalNode().getIP(),
				currentNode.getName(), currentNode.getIP());
		sendAudioInfo(currentNode, audioHandler);
		return;
	}
	
	public void accAudio(AudioHandler audioHandler){
		Node destNode = nodePool.getNode(audioHandler.sourceIP);
		Node sourceNode = nodePool.getLocalNode();
		AudioHandler accAudioHandler = new AudioHandler(DatagramPackage.KeyWord.ACCAUDIO, 
				sourceNode.getName(), sourceNode.getIP(),
				destNode.getName(), destNode.getIP());
		sendAudioInfo(destNode, accAudioHandler);
		sendAudioPacketThread = new SendAudioPacketThread(audioSocket, nodePool, senAudioDataBuffer, handler);
		sendAudioPacketThread.setAudioClient(destNode);
		sendAudioPacketThread.start();
		this.audioPlaybackThread.startPlayback();
	}
	
	public void refAudio(AudioHandler audioHandler){
		Node destNode = nodePool.getNode(audioHandler.sourceIP);
		Node sourceNode = nodePool.getLocalNode();
		AudioHandler refAudioHandler = new AudioHandler(DatagramPackage.KeyWord.REFAUDIO, 
				sourceNode.getName(), sourceNode.getIP(),
				destNode.getName(), destNode.getIP());
		sendAudioInfo(destNode, refAudioHandler);
	}
	
	public boolean endAudio(){
		Node endNode = sendAudioPacketThread.getAudioClient();
		Node sourceNode = nodePool.getLocalNode();
		AudioHandler endAudioHandler = new AudioHandler(DatagramPackage.KeyWord.ENDAUDIO, 
				sourceNode.getName(), sourceNode.getIP(),
				endNode.getName(), endNode.getIP());
		sendAudioInfo(endNode, endAudioHandler);
		sendAudioPacketThread.shutdownRequest();
		this.audioPlaybackThread.stopPlayback();
		return false;
	}
	
	public boolean sendAudioInfo(Node targetNode, AudioHandler audioHandler){
		if (targetNode == null){
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_NULL_CURRENT_CLIENT).sendToTarget();
			return false;
		} else if (!targetNode.isOnline()){
			handler.obtainMessage(TrustedNode.HANDLER_ERROR_CURRENT_CLIENT_OFFLINE).sendToTarget();
			return false;
		} else {
		byte[] data;
		DatagramPackage datagramPackage = new DatagramPackage(audioHandler.keyword,
				audioHandler.sourceIP,
				audioHandler.destIP);
			//如果是邻居节点 直接发送
			threadPool.execute(new SendPacketRunnable(datagramSocket, targetNode.getIP(), datagramPackage.toByteArray()));
			int handlerKey = -1;
			switch(audioHandler.keyword){
			case REQAUDIO:
				handlerKey = TrustedNode.HANDLER_REQ_AUDIO_SEND;
				break;
			case ACCAUDIO:
				handlerKey = TrustedNode.HANDLER_ACC_AUDIO_SEND;
				break;
			case REFAUDIO:
				handlerKey = TrustedNode.HANDLER_REF_AUDIO_SEND;
				break;
			case ENDAUDIO:
				handlerKey = TrustedNode.HANDLER_END_AUDIO_SEND;
				break;
			}
			if (handlerKey > 0){
				handler.obtainMessage(handlerKey,
						audioHandler).sendToTarget();
			}
			return true;
		}
	}
    
    public static String getLocalIP(){
    	try{
    		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
    			NetworkInterface intf = en.nextElement();
    			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
    				InetAddress inetAddress = enumIpAddr.nextElement();
    				if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().toString().contains(".")){
    					return inetAddress.getHostAddress().toString();
    				}
    			}
    		}
    	}catch (SocketException ex){
			ex.printStackTrace();
			return null;
    	}
    	return null;
    }
}
