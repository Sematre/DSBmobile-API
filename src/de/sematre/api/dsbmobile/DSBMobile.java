package de.sematre.api.dsbmobile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DSBMobile {

	private static final Gson gson = new Gson();
	private String key = "";

	public DSBMobile(String username, String password) {
		String json = "[" + getStringFromURL("https://iphone.dsbcontrol.de/(S(bsiggfwxwakskze5ca4fd4ed))/iPhoneService.svc/DSB/authid/" + username + "/" + password) + "]";
		JsonArray jArray = gson.fromJson(json, JsonArray.class);

		String key = jArray.get(0).getAsString();
		if (key.equals("00000000-0000-0000-0000-000000000000")) {
			throw new IllegalArgumentException("Username or Password is incorrect!");
		}

		this.key = key;
	}

	public ArrayList<TimeTable> getTimeTables() {
		ArrayList<TimeTable> tables = new ArrayList<>();

		String json = getStringFromURL("https://iphone.dsbcontrol.de/(S(wvecd2gt5zqqchgwjhub2x1l))/iPhoneService.svc/DSB/timetables/" + key);
		for (JsonElement jElement : gson.fromJson(json, JsonArray.class)) {
			JsonObject jObject = jElement.getAsJsonObject();
			tables.add(new TimeTable(jObject.get("ishtml").getAsBoolean(), jObject.get("timetabledate").getAsString(), jObject.get("timetablegroupname").getAsString(), jObject.get("timetabletitle").getAsString(), jObject.get("timetableurl").getAsString()));
		}

		return tables;
	}

	public ArrayList<News> getNews() {
		ArrayList<News> tables = new ArrayList<>();

		String json = getStringFromURL("https://iphone.dsbcontrol.de/(S(55rzkn1to0iidfer3akuo0xh))/iPhoneService.svc/DSB/news/" + key);
		for (JsonElement jElement : gson.fromJson(json, JsonArray.class)) {
			JsonObject jObject = jElement.getAsJsonObject();
			tables.add(new News(jObject.get("headline").getAsString(), jObject.get("newsdate").getAsString(), jObject.get("newsid").getAsString(), jObject.get("newsimageurl").getAsString(), jObject.get("shortmessage").getAsString(), jObject.get("wholemessage").getAsString()));
		}

		return tables;
	}

	private String getStringFromURL(String url) {
		String text = "";
		try {
			Scanner scanner = new Scanner(new URL(url).openStream());
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				while (line.startsWith(" ") || line.startsWith("	")) {
					line = line.substring(1);
				}

				text = text + line;
			}

			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return text;
	}
}