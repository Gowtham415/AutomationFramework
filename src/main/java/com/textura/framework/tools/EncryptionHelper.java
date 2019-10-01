package com.textura.framework.tools;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class EncryptionHelper {

	private String algorithm = "AES";
	private SecretKeySpec keySpec = null;
	private byte[] key = "<MasterPassword>".getBytes();
	private Cipher cipher = null;

	public EncryptionHelper() {
		try {
			cipher = Cipher.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		keySpec = new SecretKeySpec(key, algorithm);
	}

	public String encrypt(String input) {
		try {
			if(input == null || input.length() < 1) {
				return null;
			}
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] inputBytes = input.getBytes();
			return Base64.encodeBase64String(cipher.doFinal(inputBytes));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String decrypt(String passphrase) {
		try {
			if(passphrase == null || passphrase.length() < 1) {
				return null;
			}
			if(passphrase.equals("password")){
				return passphrase;
			}
			byte[] encryptionBytes = Base64.decodeBase64(passphrase);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] recoveredBytes = cipher.doFinal(encryptionBytes);
			String recovered = new String(recoveredBytes);
			return recovered;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}