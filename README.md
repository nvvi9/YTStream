# YTStream
[![Platform](https://img.shields.io/badge/platform-android-brightgreen.svg?style=flat)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Download](https://api.bintray.com/packages/nvvi9/maven/YTStream/images/download.svg) ](https://bintray.com/nvvi9/maven/YTStream/_latestVersion)

Android library for extracting YouTube video streaming URLs.

## Usage
This library can be used to extract both streaming URLs and video details (title, description, thumbnails, etc.), or just video details (takes less time).
Methods return Flow or Observable.
#### Kotlin Flow
```kotlin
val ytStream = YTStream()

ytStream.extractVideoData(*videoIdArray)
    .flowOn(Dispatchers.IO)
    .collect {
        doSomething(it)
    }
```
#### RxJava Observable
```java
YTStream ytStream = new YTStream();

ytStream.extractVideoDetailsObservable(videoIdArray)
    .observeOn(Schedulers.io())
    .subscribe(videoData -> {
        doSomething(videoData);
    });
```
## Installation
### Gradle
```groovy
repositories {
    ...
    maven { url 'https://dl.bintray.com/nvvi9/maven' }
}

dependencies {
    implementation 'com.nvvi9:ytstream:0.1.1'
}
```
### Maven
```xml
<dependency>
    <groupId>com.nvvi9</groupId>
    <artifactId>ytstream</artifactId>
    <version>0.1.1</version>
    <type>pom</type>
</dependency>
```
