package util;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * basic methods of the crypto and the String measures
 * **/
public class Tools {
	public static enum CRYPT_MODE {
		AES
	}
	
	public static String parseByte2HexStr(byte[] btar) {
		StringBuffer buff = new StringBuffer();
		for (byte b : btar) {
			String hex = Integer.toHexString(b);
			buff.append(hex.toUpperCase());
		}
		return buff.toString().replace("F", "");
	}
	
	public static byte[] parseHexStr2Byte(String str) {
		if (str.length() < 1) {
			return null;
		}
		byte[] result = new byte[str.length() / 2];
		for(int i=0; i<str.length(); i++) {
			int high = Integer.parseInt(str.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(str.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
	/**
	 * encrypt the byteArray 
	 * @param sourceByte the source message to encrypt
	 * @param key the cipher key
	 * @param m  the mode of this encryption: such as AES
	 * @return  the encrypt message
	 * **/
	public static byte[] encryptByte(byte[] sourceByte, byte[] key, CRYPT_MODE m) throws IOException {
		switch(m){
		case AES: 
			ByteArrayInputStream in = new ByteArrayInputStream(sourceByte);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);
			CipherInputStream cin = new CipherInputStream(in, cipher);
			byte[] buffer = new byte[1024];
			int nRead = 0;
			while((nRead = cin.read(buffer)) != -1) {
				out.write(buffer, 0, nRead);
				out.flush();
			}
			cin.close();
			in.close();
			out.close();
			return out.toByteArray();
			
		default:
			return null;
		
		}
	}
	/**
	 * 初始化AES Cipher
	 * @param key the key of AES Algorithms
	 * @param cipherMode select which mode of the Cipher: Decrypt / Encrypt / etc..
	 * @return the Cipher
	 * **/
	private static Cipher initAESCipher(byte[] key, int cipherMode) {
		SecretKeySpec ck = new SecretKeySpec(key, "AES");
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES");
			try {
				cipher.init(cipherMode, ck);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  cipher;
	}

	public static byte[] generateRandomByteArray(int length) {
		Random random = new Random();
		byte[] b= new byte[length];
		for (int i = 0; i < length; i++) {
			Integer is =  random.nextInt(9);
			b[i]=Byte.parseByte(is.toString());
		}
		return b;
	}
	public static byte[] generateRandomByteArray(){
		return generateRandomByteArray(16);
	}
}
