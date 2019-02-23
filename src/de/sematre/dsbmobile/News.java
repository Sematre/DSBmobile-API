package de.sematre.dsbmobile;

import java.io.Serializable;

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