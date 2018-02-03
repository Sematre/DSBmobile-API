package de.sematre.api.dsbmobile;

import java.io.Serializable;

public class TimeTable implements Serializable {

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((isHtml == null) ? 0 : isHtml.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
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
		return "{isHtml=" + isHtml + ", date=" + date + ", groupName=" + groupName + ", title=" + title + ", url=" + url + "}";
	}
}