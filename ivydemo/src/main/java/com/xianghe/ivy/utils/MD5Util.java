package com.xianghe.ivy.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName MD5Util
 * @Description TODO MD5加密
 * @author hj
 * @date 2015-4-3
 */
public class MD5Util {
	private static char hexDigits[] = { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };
	
	/**
	 * 密码加密MD5
	 * @param password
	 * @return
	 */
	public static String encryptPassword(String password){
		if(TextUtils.isEmpty(password)){
			return null;
		}else{
			try{
				byte[] btInput = password.getBytes();
			    //获得MD5摘要算法的 MessageDigest 对象
				MessageDigest mdInst = MessageDigest.getInstance("MD5");
			     //使用指定的字节更新摘要
				mdInst.update(btInput);
			     //获得密文
	            byte[] md = mdInst.digest();
	            //把密文转换成十六进制的字符串形式
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	/***
	 * MD5加码 生成32位md5码
	 */
	public static String string2MD5(String inStr){
		MessageDigest md5 = null;
		try{
			md5 = MessageDigest.getInstance("MD5");
		}catch (Exception e){
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++){
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();

	}
	private static final char HEX_DIGITS[] =
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] b) {
	 //String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	/**
	*  @author huangjiang
	 *
	 * @Description:xianban,MD5 32位大写
	 *
	*  Creaed 16/8/2 上午3:59
	**/

	public static String mmd5(String s) {
		try { // Create MD5 Hash
		MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
			return "";
	}
	public static String Md532(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			return buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return plainText;
	}
}
