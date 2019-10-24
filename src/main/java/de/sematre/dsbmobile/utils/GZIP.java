package de.sematre.dsbmobile.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP {

	public static byte[] compress(String data) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length());

		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
		gzipOutputStream.write(data.getBytes("UTF-8"));
		gzipOutputStream.close();

		return outputStream.toByteArray();
	}

	public static String decompress(byte[] data) throws IOException {
		GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(data));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputStream.available());
		byte[] buffer = new byte[1024];

		int len;
		while ((len = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}

		outputStream.close();
		byte[] bytes = outputStream.toByteArray();
		return new String(bytes, "UTF-8");
	}
}