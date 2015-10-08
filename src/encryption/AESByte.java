package encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESByte {
	/**
	 * 对文件进行AES加密
	 * 
	 * @param sourceByte
	 * @param byteKey
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */

	public static byte[] encryptByte(byte[] sourceByte, byte[] byteKey)
			throws IOException {

		ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceByte,
				0, sourceByte.length);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

		Cipher cipher = initAESCipher(byteKey, Cipher.ENCRYPT_MODE);
		CipherInputStream cipherInputStream = new CipherInputStream(
				inputStream, cipher);
		byte[] cache = new byte[1024];
		int nRead = 0;
		while ((nRead = cipherInputStream.read(cache)) != -1) {
			outputStream.write(cache, 0, nRead);
			outputStream.flush();
		}
		cipherInputStream.close();

		inputStream.close();
		outputStream.close();

		return outputStream.toByteArray();
	}

	/**
	 * AES方式解密文件
	 * 
	 * @param sourceByte
	 * @param byteKey
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */

	public static byte[] decryptByte(byte[] sourceByte, byte[] byteKey)
			throws IOException {

		Cipher cipher = initAESCipher(byteKey, Cipher.DECRYPT_MODE);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceByte,
				0, sourceByte.length);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
		CipherOutputStream cipherOutputStream = new CipherOutputStream(
				outputStream, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = inputStream.read(buffer)) >= 0) {
			cipherOutputStream.write(buffer, 0, r);
		}
		cipherOutputStream.close();

		inputStream.close();

		outputStream.close();

		return outputStream.toByteArray();
	}

	/**
	 * 初始化 AES Cipher
	 * 
	 * @param bytekey
	 * @param cipherMode
	 * @return
	 * @throws Exception
	 */
	public static Cipher initAESCipher(byte[] bytekey, int cipherMode) {
		// 创建Key gen
		Cipher cipher = null;
		try {
			SecretKeySpec key = getKey(bytekey);
			cipher = Cipher.getInstance("AES");
			// 初始化
			cipher.init(cipherMode, key);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static SecretKeySpec getKey(byte[] bytekey) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(bytekey, "AES");

		return skeySpec;
	}
}
