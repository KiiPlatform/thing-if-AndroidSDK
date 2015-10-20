[![Circle CI](https://circleci.com/gh/KiiPlatform/thing-if-AndroidSDK/tree/master.svg?style=svg)](https://circleci.com/gh/KiiPlatform/thing-if-AndroidSDK/tree/master)
# thing-if AndroidSDK

Android SDK for Kii Thing Interaction Framework.<br>

## Requirement

```groovy
compile 'com.android.support:appcompat-v7:22.2.0'
compile 'com.google.code.gson:gson:2.3.1'
compile 'com.squareup.okhttp:okhttp:2.4.0'
```

# Get SDK Library File

There are two ways to add the library.

* Downloading from JCenter repository
* Generating locally from source code

We recommend your project to using JCenter to let it fetch released library, but you can also generate library from source code if you would like to use the features under development from the branch other than master.

## Download from JCenter

Add the following dependencies in the "build.gradle" file that locates under the target module to build. The file should be under the "app" directory by default.

```
compile 'com.kii:thing-if:xx.yy.zzz:library@aar' // xx.yy.zz is the version number of thing-if AndroidSDK to use
```

## Manually

### Generate Locally

- Clone the source code of thing-if AndroidSDK from Github and checkout the branch to generate library from.

  ```bash
  $ git clone https://github.com/KiiPlatform/thing-if-AndroidSDK.git
  ```

- Generate locally. If generate successfully, an aar file `thingif-release*.aar` can be found under folder `thing-if-AndroidSDK/thingif/build/artifacts/`

  ```bash
  $ cd thing-if-AndroidSDK
  $ ./gradlew :thingif:makeAAR
  ```

- Copy the generated library (thingif-release*.aar) to your project's "libs" directory.

  The directory is the "libs" under your module directory. It is "app/libs" by default.

- Add the following in the "build.gradle" file that locates under the target module to build. The file should be under the "app" directory by default.

  ```xml
  repositories {
    flatDir {
      dirs 'libs'
    }
  }

  dependencies {
    compile 'com.example.library:thingif-release*@aar' // "thingif-release*" should replace with real name of the generated aar file without .aar extension
    compile 'com.google.code.gson:gson:2.3.1'
    compile 'com.squareup.okhttp:okhttp:2.4.0'
  }
  ```
# Usage

Please check the [Documentation](http://documentation.kii.com/en/starts/iotsdk/) from Kii Cloud.

# Sample Project

A sample project using thing-if AndroidSDK can be found [here](https://github.com/KiiPlatform/thing-if-AndroidSample).

# License
