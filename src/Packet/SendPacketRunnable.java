/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author footman
 */
public class SendPacketRunnable implements Runnable{
	private static final String TAG = "SendPacketRunnable";
	private final DatagramSocket datagramSocket;
	private SocketAddress destAddress;
	private byte[] data;
	
	public SendPacketRunnable(DatagramSocket socket, String destIP, byte[] data){
		this.datagramSocket = socket;
		this.destAddress = new InetSocketAddress(destIP, PacketThreadManager.RECEIVE_PORT);
		this.data = data;
	}
	
	public void run(){
		try{
			DatagramPacket outputPacket = new DatagramPacket(data, data.length, destAddress);
			datagramSocket.send(outputPacket);
		} catch (SocketException e){
			System.out.println(TAG + "数据包发送失败...目标：" + destAddress);
			e.printStackTrace();
		} catch (IOException e){
			System.out.println(TAG + "数据包创建失败...目标：" + destAddress);
			e.printStackTrace();
		}
	}
}
