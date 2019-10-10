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

	String title = timeTable.getTitle();
	String detail = timeTable.getDetail();

	String date = timeTable.getDate();
	String url = timeTable.getUrl();

}
```

### News

Work in progress.

## Implementation
Gradle:
```gradle
dependencies {
	implementation 'de.sematre.dsbmobile:DSBmobile-API:1.2'
}
```

Maven:
```xml
<dependency>
	<groupId>de.sematre.dsbmobile</groupId>
	<artifactId>DSBmobile-API</artifactId>
	<version>1.2</version>
</dependency>
```

## Release History
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
© Sematre 2018

Distributed under the **MIT License**. See ``LICENSE`` for more information.

[release-image]: https://img.shields.io/github/release/Sematre/DSBmobile-API.svg?style=flat-square
[release-url]: https://github.com/Sematre/DSBmobile-API/releases

[maven-image]: https://img.shields.io/maven-central/v/de.sematre.dsbmobile/DSBmobile-API.svg?style=flat-square
[maven-url]: https://search.maven.org/artifact/de.sematre.dsbmobile/DSBmobile-API/

[travis-image]: https://img.shields.io/travis/com/Sematre/DSBmobile-API.svg?style=flat-square
[travis-url]: https://travis-ci.com/Sematre/DSBmobile-API

[license-image]: https://img.shields.io/github/license/Sematre/DSBmobile-API.svg?style=flat-square
[license-url]: https://github.com/Sematre/DSBmobile-API/blob/master/LICENSE