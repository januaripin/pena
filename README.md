# Pena
A simple sketch pad to draw on android screen using a finger.

## Download [![](https://jitpack.io/v/januaripin/pena.svg)](https://jitpack.io/#januaripin/pena)

Add it in your root `build.gradle`

```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

And add the dependency

```groovy
dependencies {
	implementation 'com.github.januaripin:pena:$latestVersion'
}
```

## Usage
```java
Pena.create(this)
   .filenamePrefix("Pena")
   .backgroundImage("/storage/emulated/0/Pictures/Image.jpg") // local path image
   .backgroundColor(Color.WHITE) //ignore if you set background image
   .fileDirectory("Pena")
   .start();
```
## Get Result
```java
@Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
     if (Pena.hasResult(requestCode, resultCode, data)) {
         // get the image path
         String path = Pena.getFilePath(data);

         // do something with the image path here.
     }
 }
```

License
-------
   Copyright 2018 Yanuar Arifin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
