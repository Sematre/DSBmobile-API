package de.sematre.dsbmobile.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class Base64 {

	private static final char[] TABLE_ENCODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	private static final int[] TABLE_DECODE = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	public static String encode(byte[] data) {
		StringBuilder builder = new StringBuilder();
		int padding = 0;
		for (int index = 0; index < data.length; index += 3) {

			int b = ((data[index] & 0xFF) << 16) & 0xFFFFFF;
			if (index + 1 < data.length) {
				b |= (data[index + 1] & 0xFF) << 8;
			} else {
				padding++;
			}

			if (index + 2 < data.length) {
				b |= (data[index + 2] & 0xFF);
			} else {
				padding++;
			}

			for (int k = 0; k < 4 - padding; k++) {
				int c = (b & 0xFC0000) >> 18;
				builder.append(TABLE_ENCODE[c]);
				b <<= 6;
			}
		}

		for (int i = 0; i < padding; i++) {
			builder.append("=");
		}

		return builder.toString();
	}

	public static byte[] decode(String data) {
		byte[] bytes;
		try {
			bytes = data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 is not supported!", e);
		}

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length;) {
			int k = 0;
			if (TABLE_DECODE[bytes[i]] != -1) {
				k = (TABLE_DECODE[bytes[i]] & 0xFF) << 18;
			} else { // skip unknown characters
				i++;
				continue;
			}

			int num = 0;
			if (i + 1 < bytes.length && TABLE_DECODE[bytes[i + 1]] != -1) {
				k = k | ((TABLE_DECODE[bytes[i + 1]] & 0xFF) << 12);
				num++;
			}

			if (i + 2 < bytes.length && TABLE_DECODE[bytes[i + 2]] != -1) {
				k = k | ((TABLE_DECODE[bytes[i + 2]] & 0xFF) << 6);
				num++;
			}

			if (i + 3 < bytes.length && TABLE_DECODE[bytes[i + 3]] != -1) {
				k = k | (TABLE_DECODE[bytes[i + 3]] & 0xFF);
				num++;
			}

			while (num > 0) {
				int c = (k & 0xFF0000) >> 16;
				buffer.write((char) c);
				k <<= 8;
				num--;
			}

			i += 4;
		}

		return buffer.toByteArray();
	}
}