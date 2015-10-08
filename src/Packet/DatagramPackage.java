package Packet;

import client.Node;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DatagramPackage implements Serializable{
	
	public enum KeyWord{
		/**
		 * 心跳包
		 */
		HEART_BEAT,
		/**
		 * 文字信息
		 */
		TEXT,
		/**
		 * 语音邀请信息
		 */
		REQAUDIO,
		/**
		 * 语音同意信息
		 */
		ACCAUDIO,
		/**
		 * 语音拒绝信息
		 */
		REFAUDIO,
		/**
		 * 语音结束信息
		 */
		ENDAUDIO,
		/**
		 * 语音通信数据
		 */
		AUDIO,
		/**
		 * 位置信息
		 */
		LOCATION,
		
		/**
		 * 接入协议步骤
		 */
		RSA_PACKAGE0,
		RSA_PACKAGE1,
		RSA_PACKAGE2,
		RSA_PACKAGE3,
		RSA_PACKAGE4,
		PACKAGE0,
		PACKAGE1,
		PACKAGE2,
		PACKAGE3,
		PACKAGE4,
		QPACKAGE1,
		QPACKAGE2,
		QPACKAGE3,
		UPDATE_HEARTBEART,
		DELETE_CLIENT,
		ADD_CLIENT,
	}
	
	private static final long serialVersionUID = 101L;
	private static final int DATA_LENGTH = 1500;
	private final KeyWord keyWord;
	private final String sourceIP;
	private final String destIP;
	private byte[] byteData;
	private boolean booleanData;
	
	private String[] strarr_data;
	private boolean[] boolarr_data;

	private long longdata;
	
	
	
	/**
	 * 创建一个新的DatagramPackage
	 * @param keyword Package关键字
	 * @param sourceIP Package源IP地址
	 * 
	 * @param destIP Package目的IP地址
	 */
	public DatagramPackage(KeyWord keyword, String sourceIP, String destIP){
		this.keyWord = keyword;
		this.sourceIP = sourceIP;
		this.destIP = destIP;
	}
	public byte[] getByteData() {
		return byteData;
	}

	public String getDestIP(){
		return this.destIP;
	}
	
	public String getSourceIP(){
		return this.sourceIP;
	}
	
	public void addByteData(byte[] data){
		this.byteData = data;
	}
	

	public void addBooleanArrayData(boolean[] data){
		this.boolarr_data = data;
	}


	public boolean[] getBooleanArrayData(){
		return this.boolarr_data;
	}
	
	public String[] getStringArrayData(){
		return this.strarr_data;
	}
	
	public KeyWord getKeyWord(){
		return this.keyWord;
	}

	public byte[] toByteArray() {
		ByteArrayOutputStream baos= new ByteArrayOutputStream(DATA_LENGTH);
		try{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			byte[] result = baos.toByteArray();
			oos.close();
			baos.close();
			return result;
		} catch (IOException e){
			e.printStackTrace();
			throw new RuntimeException("Cannot get ObjectOutputStream from DatagramPackage class");
		}
	}
}
