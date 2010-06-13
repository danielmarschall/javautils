package de.viathinksoft.utils.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String digest(String input) throws NoSuchAlgorithmException {
		if (input == null) input = "";
		
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(input.getBytes());
		byte[] result = md5.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			if (result[i] <= 15 && result[i] >= 0) {
				hexString.append("0");
			}
			hexString.append(Integer.toHexString(0xFF & result[i]));
		}

		return hexString.toString();
	}

	public static String digest(String input, String salt)
			throws NoSuchAlgorithmException {
		if (salt == null) salt = "";
		return digest(input.concat(salt));
	}
}
