# DSBmobile-API
[![Release Version][release-image]][release-url]
[![Maven Version][maven-image]][maven-url]
[![Build Status][travis-image]][travis-url]
[![License][license-image]][license-url]
> Unofficial DSBmobile API for Java.


A simple Java library for DSBmobile.

## Code example

### TimeTable
```java
DSBMobile dsbMobile = new DSBMobile("username", "password");

ArrayList<TimeTable> timeTables = dsbMobile.getTimeTables();
for (TimeTable timeTable : timeTables) {

	UUID uuid = timeTable.getUUID();

	String groupName = timeTable.getGroupName();
	String date = timeTable.getDate();

	String title = timeTable.getTitle();
	String detail = timeTable.getDetail();

}
```

### News
```java
DSBMobile dsbMobile = new DSBMobile("username", "password");

ArrayList<News> newsList = dsbMobile.getNews();
for (News news : newsList) {

	UUID uuid = news.getUUID();
	String date = news.getDate();

	String title = news.getTitle();
	String detail = news.getDetail();

}
```

## Implementation
Gradle:
```gradle
dependencies {
	implementation 'de.sematre.dsbmobile:DSBmobile-API:1.8'
}
```

Maven:
```xml
<dependency>
	<groupId>de.sematre.dsbmobile</groupId>
	<artifactId>DSBmobile-API</artifactId>
	<version>1.8</version>
</dependency>
```

## Release History
* 1.8
	* News implementation
* 1.7
	* Make UUID random
* 1.6
	* Performance improvement
* 1.5
	* Web interface update
	* Fix: Inner tables are now included
* 1.4
	* Change time zone formatting: ISO 8601 -> RFC 822
	* Make GZIP methods private
	* Merge: Base64 -> DSBMobile
* 1.3
	* Web handler update
* 1.2
    * Maven implementation
* 1.1
    * Code cleanup
	* Readme update
* 1.0
    * Initial version

## Dependencies
- [Google Gson](https://github.com/google/gson) ([Apache 2 license](https://github.com/google/gson/blob/master/LICENSE))

## Info
© Sematre 2019

Distributed under the **MIT License**. See ``LICENSE`` for more information.

[release-image]: https://img.shields.io/github/release/Sematre/DSBmobile-API.svg?style=flat-square
[release-url]: https://github.com/Sematre/DSBmobile-API/releases

[maven-image]: https://img.shields.io/maven-central/v/de.sematre.dsbmobile/DSBmobile-API.svg?style=flat-square
[maven-url]: https://search.maven.org/artifact/de.sematre.dsbmobile/DSBmobile-API/

[travis-image]: https://img.shields.io/travis/com/Sematre/DSBmobile-API.svg?style=flat-square
[travis-url]: https://travis-ci.com/Sematre/DSBmobile-API

[license-image]: https://img.shields.io/github/license/Sematre/DSBmobile-API.svg?style=flat-square
[license-url]: https://github.com/Sematre/DSBmobile-API/blob/master/LICENSE