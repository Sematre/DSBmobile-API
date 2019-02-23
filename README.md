# DSBmobile-API
[![Release Version][release-image]][release-url]
[![Build Status][travis-image]][travis-url]
[![License][license-image]][license-url]
> Unofficial DSBmobile API for Java.


A simple Java library for DSBmobile.

## Code example

### TimeTable
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
	// Wrong username or password!
}
```
Note: The try-catch block is not necessary.

### News
```java
try {
	DSBMobile dsbMobile = new DSBMobile("username", "password");

	ArrayList<News> newsList = dsbMobile.getNews();
	for (News news : newsList) {

		String id = news.getId();
		String headLine = news.getHeadLine();
		String date = news.getDate();
		String imageUrl = news.getImageUrl();
		String shortMessage = news.getShortMessage();
		String wholeMessage = news.getWholeMessage();

	}
} catch (IllegalArgumentException e) {
	// Wrong username or password!
}
```
Note: The try-catch block is not necessary.

## Release History
* 1.1
    * Code cleanup
	* Readme update
* 1.0
    * Initial version

## Dependencies
- [Google Gson](https://github.com/google/gson) ([Apache 2 license](https://github.com/google/gson/blob/master/LICENSE)).

## Info
© Sematre 2018

Distributed under the **MIT License**. See ``LICENSE`` for more information.

[release-image]: https://img.shields.io/github/release/Sematre/DSBmobile-API.svg?style=flat-square
[release-url]: https://github.com/Sematre/DSBmobile-API/releases

[travis-image]: https://img.shields.io/travis/com/Sematre/DSBmobile-API.svg?style=flat-square
[travis-url]: https://travis-ci.com/Sematre/DSBmobile-API

[license-image]: https://img.shields.io/github/license/Sematre/DSBmobile-API.svg?style=flat-square
[license-url]: https://github.com/Sematre/DSBmobile-API/blob/master/LICENSE