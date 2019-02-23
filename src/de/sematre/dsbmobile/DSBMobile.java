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
		if (json == null) throw new IllegalArgumentException("Username or password is incorrect!");

		JsonArray jArray = gson.fromJson(("[" + json + "]"), JsonArray.class);
		String key = jArray.get(0).getAsString();
		if (key.equals("00000000-0000-0000-0000-000000000000")) throw new IllegalArgumentException("Username or password is incorrect!");
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
}