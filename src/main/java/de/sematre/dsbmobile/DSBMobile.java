package de.sematre.dsbmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.sematre.dsbmobile.utils.Base64;
import de.sematre.dsbmobile.utils.GZIP;

public class DSBMobile implements Serializable, Cloneable {

	private static final long serialVersionUID = -5265820858352981519L;
	private static final Gson gson = new Gson();

	private HashMap<String, Object> args = new HashMap<String, Object>();

	public DSBMobile(String username, String password) {
		args.put("UserId", username);
		args.put("UserPw", password);
		args.put("Language", "de");

		args.put("Device", "Nexus 4");
		args.put("AppId", UUID.randomUUID().toString());
		args.put("AppVersion", "2.5.9");
		args.put("OsVersion", "27 8.1.0");

		args.put("PushId", "");
		args.put("BundleId", "de.heinekingmedia.dsbmobile");
	}

	public ArrayList<TimeTable> getTimeTables() {
		JsonObject mainObject = pullData();

		int resultCode = mainObject.get("Resultcode").getAsInt();
		if (resultCode != 0) throw new RuntimeException("Server returned result code is " + resultCode + ": " + mainObject.get("ResultStatusInfo").getAsString());

		JsonObject contentObject = findJsonObjectByTitle(mainObject.get("ResultMenuItems").getAsJsonArray(), "Inhalte");
		Objects.requireNonNull(contentObject, "Server response doesn't contain content!");

		JsonObject tableObject = findJsonObjectByTitle(contentObject.get("Childs").getAsJsonArray(), "Pläne");
		Objects.requireNonNull(tableObject, "Server response doesn't contain a table!");

		ArrayList<TimeTable> tables = new ArrayList<>();
		for (JsonElement jElement : tableObject.get("Root").getAsJsonObject().get("Childs").getAsJsonArray()) {
			if (!jElement.isJsonObject()) continue;
			JsonObject jObject = jElement.getAsJsonObject();

			UUID uuid = UUID.fromString(jObject.get("Id").getAsString());
			String groupName = jObject.get("Title").getAsString();
			String date = jObject.get("Date").getAsString();

			for (JsonElement jElementChild : jObject.get("Childs").getAsJsonArray()) {
				if (!jElementChild.isJsonObject()) continue;
				JsonObject childObject = jElementChild.getAsJsonObject();

				String title = childObject.get("Title").getAsString();
				String detail = childObject.get("Detail").getAsString();

				tables.add(new TimeTable(uuid, groupName, date, title, detail));
			}
		}

		return tables;
	}

	public ArrayList<News> getNews() {
		JsonObject mainObject = pullData();

		int resultCode = mainObject.get("Resultcode").getAsInt();
		if (resultCode != 0) throw new RuntimeException("Server returned result code is " + resultCode + ": " + mainObject.get("ResultStatusInfo").getAsString());

		JsonObject contentObject = findJsonObjectByTitle(mainObject.get("ResultMenuItems").getAsJsonArray(), "Inhalte");
		Objects.requireNonNull(contentObject, "Server response doesn't contain content!");

		JsonObject tableObject = findJsonObjectByTitle(contentObject.get("Childs").getAsJsonArray(), "News");
		Objects.requireNonNull(tableObject, "Server response doesn't contain news!");

		ArrayList<News> news = new ArrayList<>();
		for (JsonElement jElement : tableObject.get("Root").getAsJsonObject().get("Childs").getAsJsonArray()) {
			if (!jElement.isJsonObject()) continue;
			JsonObject jObject = jElement.getAsJsonObject();

			UUID uuid = UUID.fromString(jObject.get("Id").getAsString());
			String date = jObject.get("Date").getAsString();

			String title = jObject.get("Title").getAsString();
			String detail = jObject.get("Detail").getAsString();

			news.add(new News(uuid, date, title, detail));
		}

		return news;
	}

	public JsonObject pullData() {
		try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.dsbmobile.de/JsonHandler.ashx/GetData").openConnection();
			connection.setRequestMethod("POST");
			connection.addRequestProperty("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 8.1.0; Nexus 4 Build/OPM7.181205.001)");
			connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.addRequestProperty("Content-Type", "application/json;charset=utf-8");

			connection.setDoOutput(true);
			connection.getOutputStream().write(packageArgs().getBytes("UTF-8"));

			StringBuilder builder = new StringBuilder();
			Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			for (int c; (c = in.read()) >= 0;) {
				builder.append((char) c);
			}

			return gson.fromJson(GZIP.decompress(Base64.decode(gson.fromJson(builder.toString(), JsonObject.class).get("d").getAsString())), JsonObject.class);
		} catch (IOException e) {
			throw new RuntimeException("Unable to pull data from server!", e);
		}
	}

	private String packageArgs() throws IOException {
		String date = getFormattedTime(new Date());
		args.put("Date", date);
		args.put("LastUpdate", date);

		HashMap<String, Object> innerArgs = new HashMap<String, Object>();
		innerArgs.put("Data", Base64.encode(GZIP.compress(unescapeString(gson.toJson(args)))));
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

	private String getFormattedTime(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH).format(date);
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
		private String groupName = "";
		private String date = "";

		private String title = "";
		private String detail = "";

		public TimeTable(UUID uuid, String groupName, String date, String title, String detail) {
			this.uuid = uuid;
			this.groupName = groupName;
			this.date = date;

			this.title = title;
			this.detail = detail;
		}

		public UUID getUUID() {
			return uuid;
		}

		public void setUUID(UUID uuid) {
			this.uuid = uuid;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
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
			if (groupName == null) {
				if (other.groupName != null) return false;
			} else if (!groupName.equals(other.groupName)) return false;
			if (title == null) {
				if (other.title != null) return false;
			} else if (!title.equals(other.title)) return false;
			if (uuid == null) {
				if (other.uuid != null) return false;
			} else if (!uuid.equals(other.uuid)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "{\"uuid\": \"" + uuid + "\", \"groupName\": \"" + groupName + "\", \"date\": \"" + date + "\", \"title\": \"" + title + "\", \"detail\": \"" + detail + "\"}";
		}
	}

	public class News implements Serializable, Cloneable {

		private static final long serialVersionUID = 2336407351548626614L;

		private UUID uuid = null;
		private String date = "";

		private String title = "";
		private String detail = "";

		public News(UUID uuid, String date, String title, String detail) {
			this.uuid = uuid;
			this.date = date;

			this.title = title;
			this.detail = detail;
		}

		public UUID getUUID() {
			return uuid;
		}

		public void setUUID(UUID uuid) {
			this.uuid = uuid;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
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

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			News other = (News) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance())) return false;
			if (date == null) {
				if (other.date != null) return false;
			} else if (!date.equals(other.date)) return false;
			if (detail == null) {
				if (other.detail != null) return false;
			} else if (!detail.equals(other.detail)) return false;
			if (title == null) {
				if (other.title != null) return false;
			} else if (!title.equals(other.title)) return false;
			if (uuid == null) {
				if (other.uuid != null) return false;
			} else if (!uuid.equals(other.uuid)) return false;
			return true;
		}

		private DSBMobile getEnclosingInstance() {
			return DSBMobile.this;
		}

		@Override
		public String toString() {
			return "{\"uuid\": \"" + uuid + "\", \"date\": \"" + date + "\", \"title\": \"" + title + "\", \"detail\": \"" + detail + "\"}";
		}
	}
}