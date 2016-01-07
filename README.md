[![Circle CI](https://circleci.com/gh/KiiPlatform/thing-if-AndroidSDK/tree/master.svg?style=svg)](https://circleci.com/gh/KiiPlatform/thing-if-AndroidSDK/tree/master)
# thing-if AndroidSDK

Android SDK for Kii Thing Interaction Framework.<br>

## Requirement

```groovy
compile 'com.android.support:appcompat-v7:22.2.0'
compile 'com.google.code.gson:gson:2.3.1'
compile 'com.squareup.okhttp:okhttp:2.4.0'
```

# Add SDK Library

There are two ways to add the library to your project.

* Downloading from JCenter repository
* Import downloaded thing-if AndroidSDK project as library module

We recommend your project to using JCenter to let it fetch released library, but you can import the downloaded project as library module if you would like to use the features under development from the branch other than master.

## Download from JCenter

Add the following dependencies in the "build.gradle" file that locates under the target module to build. The file should be under the "app" directory by default.

```
compile 'com.kii:thing-if-sdk:xx.yy.zzz:library@aar' // xx.yy.zz is the version number of thing-if AndroidSDK to use
```

## Import thing-if AndroidSDK as Library Module

- Clone the source code of thing-if AndroidSDK project from Github and checkout the appropriate branch.

  ```bash
  $ git clone https://github.com/KiiPlatform/thing-if-AndroidSDK.git
  ```

- Import it to your project as library module, then add the dependency of thing-if AndroidSDK to the "build.gradle" file locating under the target module.

# Sample Project

A sample project using thing-if AndroidSDK can be found [here](https://github.com/KiiPlatform/thing-if-AndroidSample).

# License

thing-if AndroidSDK is released under the MIT license. See LICENSE for details.
