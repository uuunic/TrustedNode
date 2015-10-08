package Packet;

import java.io.IOException;
import java.net.*;

public class ServerSendAccessPacketRunnable  implements Runnable{
	private static final String TAG = "SendAccessPacketRunnable";
	private final DatagramSocket datagramSocket;
	private SocketAddress destAddress;
	private byte[] data;
	
	public ServerSendAccessPacketRunnable(DatagramSocket socket, String destIP, byte[] data){
		this.datagramSocket = socket;
		this.destAddress = new InetSocketAddress(destIP, PacketThreadManager.TRUST_ACCESS_PORT);
		this.data = data;
	}
	
	public void run(){
		try{
			DatagramPacket outputPacket = new DatagramPacket(data, data.length, destAddress);
			datagramSocket.send(outputPacket);
		} catch (SocketException e){
			System.out.println("数据包发送失败...目标：" + destAddress);
			e.printStackTrace();
		} catch (IOException e){
			System.out.println("数据包创建失败...目标：" + destAddress);
			e.printStackTrace();
		}
	}
}
