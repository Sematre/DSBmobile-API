# DSBmobile-API
Version: 1.0
Eine Java API für DSBmobile.

## Wird benötigt

### Google-Gson

##### [https://github.com/google/gson](https://github.com/google/gson)

## Benutzung

### Stundenpläne auslesen
##### Imports:
```java
import de.sematre.api.dsbmobile.DSBMobile;
import de.sematre.api.dsbmobile.TimeTable;
```

##### Code:

Note:
Der try-catch Block ist hier nur Optimal!

```java
try {
	DSBMobile dsbMobile = new DSBMobile("username", "password");

	ArrayList<TimeTable> timeTables = dsbMobile.getTimeTables();
	for (TimeTable timeTable : timeTables) {

		Boolean isHtml = timeTable.IsHtml();

		String date = timeTable.getDate();
		String groupName = timeTable.getGroupName();
		String title = timeTable.getTitle();
		String url = timeTable.getUrl();

	}
} catch (IllegalArgumentException e) {
	// Benutzername oder Passwort falsch!
}
```

### News auslesen
##### Imports:
```java
import de.sematre.api.dsbmobile.DSBMobile;
import de.sematre.api.dsbmobile.News;
```

##### Code:

Note:
Der try-catch Block ist auch hier nur Optimal!

```java
try {
	DSBMobile dsbMobile = new DSBMobile("username", "password");

	ArrayList<News> newsList = dsbMobile.getNews();
	for (News news : newsList) {

		String headLine = news.getHeadLine();
		String date = news.getDate();
		String id = news.getId();
		String imageUrl = news.getImageUrl();
		String shortMessage = news.getShortMessage();
		String wholeMessage = news.getWholeMessage();

	}
} catch (IllegalArgumentException e) {
	// Benutzername oder Passwort falsch!
}
```