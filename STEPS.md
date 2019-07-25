# GymCompanion Dev Steps

This is an (hopefully) exhaustive list of the steps to produce a working project in Android Studio from the files in this repository from zero

## gradle stuff
- add ``maven { url 'https://jitpack.io' }`` to ``allprojects{repositories{...}}`` in gradle.build 
- add dependencies to app/build.gradle: 
``` 
	implementation 'com.github.TutorialsAndroid:FilePicker:v1.0.19'
    implementation 'commons-io:commons-io:2.6'
```

## resources stuff
- replace colors.xml, strings.xml from res/values
- copy string-arrays.xml to res/values
- on the project navigator, right-click on strings.xml, choose Open Translations Editor and add Locale Spanish, Spain
- copy over files from/to values-xx
- create raw android resource folder and copy over contents
- create menu android resource folder and copy over contents
- add elements to drawable (drawable -> new image assset):
	- @drawable/ic_add_box_black_24dp
	- @drawable/ic_settings_applications_black_24dp
	- or copy them over

## update AndroidManifest.xml
- add the following before <application/>
```
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_INTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_INTERNAL_STORAGE" />
```

## Activities files
- copy over activities
- beware of possible package name change
- beware of differences in imports, create new activities and copy code if needed
- for layouts, beware top constrinat layout declaration




