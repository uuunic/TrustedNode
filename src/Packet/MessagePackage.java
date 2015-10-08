/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packet;

import java.io.*;

/**
 *
 * @author footman
 */
public class MessagePackage implements Serializable{
	private static final long serialVersionUID = 102L;
	private byte[] data;
	
	public MessagePackage(byte[] data){
		this.data = data;
	}
	
	public byte[] getData(){
		return this.data;
	}
	
	public byte[] toByteArray() {
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		try{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			byte[] result = baos.toByteArray();
			oos.close();
			baos.close();
			return result;
		} catch (IOException e){
			throw new RuntimeException("Cannot get ObjectOutputStream from MessagePackage class");
		}
	}
	
	public static byte[] StringToByteArray(String text){
		try {
			return text.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static String ByteArrayToString(byte[] text){
		try{
			return new String(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
