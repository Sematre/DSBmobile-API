package de.sematre.dsbmobile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DSBMobile implements Serializable, Cloneable {

	private static final long serialVersionUID = -5265820858352981519L;
	private static final String URL_PREFIX = "https://iphone.dsbcontrol.de/iPhoneService.svc/DSB";
	private static final Gson gson = new Gson();
	private String key = "";

	public DSBMobile(String key) {
		this.key = key;
	}

	public DSBMobile(String username, String password) {
		String json = getStringFromURL(URL_PREFIX + "/authid/" + username + "/" + password);
		if (json == null) throw new IllegalArgumentException("Wrong username or password");

		JsonArray jArray = gson.fromJson(("[" + json + "]"), JsonArray.class);
		String key = jArray.get(0).getAsString();
		if (key.equals("00000000-0000-0000-0000-000000000000")) throw new IllegalArgumentException("Wrong username or password");
		this.key = key;
	}

	public ArrayList<TimeTable> getTimeTables() {
		ArrayList<TimeTable> tables = new ArrayList<>();

		String json = getStringFromURL(URL_PREFIX + "/timetables/" + key);
		for (JsonElement jElement : gson.fromJson(json, JsonArray.class)) {
			JsonObject jObject = jElement.getAsJsonObject();
			tables.add(new TimeTable(jObject.get("ishtml").getAsBoolean(), jObject.get("timetabledate").getAsString(), jObject.get("timetablegroupname").getAsString(), jObject.get("timetabletitle").getAsString(), jObject.get("timetableurl").getAsString()));
		}

		return tables;
	}

	public ArrayList<News> getNews() {
		ArrayList<News> tables = new ArrayList<>();

		String json = getStringFromURL(URL_PREFIX + "/news/" + key);
		for (JsonElement jElement : gson.fromJson(json, JsonArray.class)) {
			JsonObject jObject = jElement.getAsJsonObject();
			tables.add(new News(jObject.get("headline").getAsString(), jObject.get("newsdate").getAsString(), jObject.get("newsid").getAsString(), jObject.get("newsimageurl").getAsString(), jObject.get("shortmessage").getAsString(), jObject.get("wholemessage").getAsString()));
		}

		return tables;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private String getStringFromURL(String url) {
		try {
			String text = "";
			Scanner scanner = new Scanner(new URL(url).openStream());
			while (scanner.hasNextLine()) {
				text += scanner.nextLine().trim();
			}

			scanner.close();
			return text;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public class TimeTable implements Serializable, Cloneable {

		private static final long serialVersionUID = 553852884423090700L;
		private Boolean isHtml = false;
		private String date = "";
		private String groupName = "";
		private String title = "";
		private String url = "";

		public TimeTable(Boolean isHtml, String date, String groupName, String title, String url) {
			this.isHtml = isHtml;
			this.date = date;
			this.groupName = groupName;
			this.title = title;
			this.url = url;
		}

		public Boolean IsHtml() {
			return isHtml;
		}

		public void setIsHtml(Boolean isHtml) {
			this.isHtml = isHtml;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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
			if (groupName == null) {
				if (other.groupName != null) return false;
			} else if (!groupName.equals(other.groupName)) return false;
			if (isHtml == null) {
				if (other.isHtml != null) return false;
			} else if (!isHtml.equals(other.isHtml)) return false;
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
			return "{\"isHtml\":\"" + isHtml + "\", \"date\":\"" + date + "\", \"groupName\":\"" + groupName + "\", \"title\":\"" + title + "\", \"url\":\"" + url + "\"}";
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
}