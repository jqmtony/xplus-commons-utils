package org.xplus.commons.utils.encrypt;

import junit.framework.TestCase;

public class DESUtilsTest extends TestCase {

	public void testHexString2Bytes() {
		// fail("Not yet implemented");
	}

	public void testEncrypt() {
		// fail("Not yet implemented");
		String source = "amigoxie";
		System.out.println("原文: " + source);
		String key = "A1B2C3D4E5F60708";

		try {
			String encryptData = DESUtils.encrypt(source, key);
			System.out.println("加密后: " + encryptData);
			String decryptData = DESUtils.decrypt(encryptData, key);
			System.out.println("解密后: " + decryptData);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void testDecrypt() {
		// fail("Not yet implemented");
		String source = "amigoxie";
		System.out.println("原文: " + source);
		String key = "A1B2C3D4E5F60708";

		try {
			String encryptData = DESUtils.encrypt(source, key);
			System.out.println("加密后: " + encryptData);
			String decryptData = DESUtils.decrypt(encryptData, key);
			System.out.println("解密后: " + decryptData);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
