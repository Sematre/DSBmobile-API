package de.sematre.dsbmobile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DSBMobile implements Serializable, Cloneable {

	private static final long serialVersionUID = -5265820858352981519L;
	private static final Gson gson = new Gson();

	private HashMap<String, Object> args = new HashMap<String, Object>();

	public DSBMobile(String username, String password) {
		args.put("UserId", username);
		args.put("UserPw", password);
		args.put("Abos", new ArrayList<String>());
		args.put("AppVersion", "2.3");
		args.put("Language", "de");
		args.put("OsVersion", "");
		args.put("AppId", "");
		args.put("Device", "WebApp");
		args.put("PushId", "");
		args.put("BundleId", "de.heinekingmedia.inhouse.dsbmobile.web");
	}

	public ArrayList<TimeTable> getTimeTables() {
		try {
			JsonObject mainObject = findJsonObjectByTitle(pullData().get("ResultMenuItems").getAsJsonArray(), "Inhalte");
			Objects.requireNonNull(mainObject, "Server data doesn't contain content!");

			JsonObject jObject = findJsonObjectByTitle(mainObject.get("Childs").getAsJsonArray(), "Pläne");
			Objects.requireNonNull(jObject, "Server data doesn't contain a time table!");

			ArrayList<TimeTable> tables = new ArrayList<>();
			for (JsonElement jElement : jObject.get("Root").getAsJsonObject().get("Childs").getAsJsonArray()) {
				tables.add(new TimeTable(jElement.getAsJsonObject()));
			}

			return tables;
		} catch (IOException e) {
			throw new RuntimeException("Unable to pull data from server!", e);
		}
	}

	@Deprecated
	public ArrayList<News> getNews() {
		throw new UnsupportedOperationException("Not implemented, yet!");
	}

	public JsonObject pullData() throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.dsbmobile.de/JsonHandlerWeb.ashx/GetData").openConnection();
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "*/*");
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0");
		connection.addRequestProperty("Content-Type", "application/json;charset=utf-8");
		connection.addRequestProperty("Bundle_ID", "de.heinekingmedia.inhouse.dsbmobile.web");
		connection.addRequestProperty("Referer", "https://www.dsbmobile.de/default.aspx");

		connection.setDoOutput(true);
		connection.getOutputStream().write(packageArgs().getBytes("UTF-8"));

		StringBuilder builder = new StringBuilder();
		Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		for (int c; (c = in.read()) >= 0;) {
			builder.append((char) c);
		}

		return gson.fromJson(decompressGZIP(Base64.decode(gson.fromJson(builder.toString(), JsonObject.class).get("d").getAsString())), JsonObject.class);
	}

	private String packageArgs() throws IOException {
		String date = getJavascriptTime(new Date());
		args.put("Date", date);
		args.put("LastUpdate", date);

		HashMap<String, Object> innerArgs = new HashMap<String, Object>();
		innerArgs.put("Data", Base64.encode(compressGZIP(unescapeString(gson.toJson(args)))));
		innerArgs.put("DataType", 1);

		HashMap<String, Object> outerArgs = new HashMap<String, Object>();
		outerArgs.put("req", innerArgs);
		return unescapeString(gson.toJson(outerArgs));
	}

	private JsonObject findJsonObjectByTitle(JsonArray sourceArray, String title) {
		for (JsonElement jElement : sourceArray) {
			if (!jElement.isJsonObject()) continue;
			JsonObject jObject = jElement.getAsJsonObject();

			if (!jObject.has("Title")) continue;
			String objectTitle = jObject.get("Title").getAsString();

			if (objectTitle.equalsIgnoreCase(title)) return jObject;
		}

		return null;
	}

	private String getJavascriptTime(Date date) {
		return new SimpleDateFormat("E MMM dd yyyy HH:mm:ss XX", Locale.ENGLISH).format(date);
	}

	public static byte[] compressGZIP(String data) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length());

		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
		gzipOutputStream.write(data.getBytes("UTF-8"));
		gzipOutputStream.close();

		return outputStream.toByteArray();
	}

	public static String decompressGZIP(byte[] data) throws IOException {
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

	private String unescapeString(String text) {
		StringBuilder builder = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '\\') {
				char nextChar = (i == text.length() - 1) ? '\\' : text.charAt(i + 1);

				// Octal escape?
				if (nextChar >= '0' && nextChar <= '7') {
					String code = "" + nextChar;
					i++;

					if ((i < text.length() - 1) && text.charAt(i + 1) >= '0' && text.charAt(i + 1) <= '7') {
						code += text.charAt(i + 1);
						i++;

						if ((i < text.length() - 1) && text.charAt(i + 1) >= '0' && text.charAt(i + 1) <= '7') {
							code += text.charAt(i + 1);
							i++;
						}
					}

					builder.append((char) Integer.parseInt(code, 8));
					continue;
				}

				switch (nextChar) {
					case '\\':
						ch = '\\';
						break;
					case 'b':
						ch = '\b';
						break;
					case 'f':
						ch = '\f';
						break;
					case 'n':
						ch = '\n';
						break;
					case 'r':
						ch = '\r';
						break;
					case 't':
						ch = '\t';
						break;
					case '\"':
						ch = '\"';
						break;
					case '\'':
						ch = '\'';
						break;

					// Hex Unicode: u????
					case 'u':
						if (i >= text.length() - 5) {
							ch = 'u';
							break;
						}

						int code = Integer.parseInt("" + text.charAt(i + 2) + text.charAt(i + 3) + text.charAt(i + 4) + text.charAt(i + 5), 16);
						builder.append(Character.toChars(code));
						i += 5;
						continue;
				}

				i++;
			}

			builder.append(ch);
		}

		return builder.toString();
	}

	public class TimeTable implements Serializable, Cloneable {

		private static final long serialVersionUID = 553852884423090700L;
		private UUID uuid = null;
		private String title = "";
		private String detail = "";
		private String date = "";
		private String url = "";

		public TimeTable(UUID uuid, String title, String detail, String date, String url) {
			this.uuid = uuid;
			this.title = title;
			this.detail = detail;
			this.date = date;
			this.url = url;
		}

		public TimeTable(JsonObject jsonObject) {
			this.uuid = UUID.fromString(jsonObject.get("Id").getAsString());
			this.title = jsonObject.get("Title").getAsString();
			this.detail = jsonObject.get("Detail").getAsString();
			this.date = jsonObject.get("Date").getAsString();
			this.url = jsonObject.get("Childs").getAsJsonArray().get(0).getAsJsonObject().get("Detail").getAsString();
		}

		public UUID getUUID() {
			return uuid;
		}

		public void setUUID(UUID uuid) {
			this.uuid = uuid;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			TimeTable other = (TimeTable) obj;
			if (date == null) {
				if (other.date != null) return false;
			} else if (!date.equals(other.date)) return false;
			if (detail == null) {
				if (other.detail != null) return false;
			} else if (!detail.equals(other.detail)) return false;
			if (uuid == null) {
				if (other.uuid != null) return false;
			} else if (!uuid.equals(other.uuid)) return false;
			if (title == null) {
				if (other.title != null) return false;
			} else if (!title.equals(other.title)) return false;
			if (url == null) {
				if (other.url != null) return false;
			} else if (!url.equals(other.url)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "{\"uuid\":\"" + uuid + "\", \"date\":\"" + date + "\", \"detail\":\"" + detail + "\", \"title\":\"" + title + "\", \"url\":\"" + url + "\"}";
		}
	}

	public class News implements Serializable, Cloneable {

		private static final long serialVersionUID = 2336407351548626614L;
		private String headLine = "";
		private String date = "";
		private String id = "";
		private String imageUrl = "";
		private String shortMessage = "";
		private String wholeMessage = "";

		public News(String headLine, String date, String id, String imageUrl, String shortMessage, String wholeMessage) {
			this.headLine = headLine;
			this.date = date;
			this.id = id;
			this.imageUrl = imageUrl;
			this.shortMessage = shortMessage;
			this.wholeMessage = wholeMessage;
		}

		public News(JsonObject jsonObject) {
			this.headLine = jsonObject.get("headline").getAsString();
			this.date = jsonObject.get("newsdate").getAsString();
			this.id = jsonObject.get("newsid").getAsString();
			this.imageUrl = jsonObject.get("newsimageurl").getAsString();
			this.shortMessage = jsonObject.get("shortmessage").getAsString();
			this.wholeMessage = jsonObject.get("wholemessage").getAsString();
		}

		public String getHeadLine() {
			return headLine;
		}

		public void setHeadLine(String headLine) {
			this.headLine = headLine;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getShortMessage() {
			return shortMessage;
		}

		public void setShortMessage(String shortMessage) {
			this.shortMessage = shortMessage;
		}

		public String getWholeMessage() {
			return wholeMessage;
		}

		public void setWholeMessage(String wholeMessage) {
			this.wholeMessage = wholeMessage;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			News other = (News) obj;
			if (date == null) {
				if (other.date != null) return false;
			} else if (!date.equals(other.date)) return false;
			if (headLine == null) {
				if (other.headLine != null) return false;
			} else if (!headLine.equals(other.headLine)) return false;
			if (id == null) {
				if (other.id != null) return false;
			} else if (!id.equals(other.id)) return false;
			if (imageUrl == null) {
				if (other.imageUrl != null) return false;
			} else if (!imageUrl.equals(other.imageUrl)) return false;
			if (shortMessage == null) {
				if (other.shortMessage != null) return false;
			} else if (!shortMessage.equals(other.shortMessage)) return false;
			if (wholeMessage == null) {
				if (other.wholeMessage != null) return false;
			} else if (!wholeMessage.equals(other.wholeMessage)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "{\"headLine\":\"" + headLine + "\", \"date\":\"" + date + "\", \"id\":\"" + id + "\", \"imageUrl\":\"" + imageUrl + "\", \"shortMessage\":\"" + shortMessage + "\", \"wholeMessage\":\"" + wholeMessage + "\"}";
		}
	}
	
	private static class Base64 {

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
}