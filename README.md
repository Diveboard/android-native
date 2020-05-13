#################################
##   Diveboard Android Build   ##
#################################


# Prerequisites

You must ensure that the following packages are installed:
* Android SDK
* Android Studio (optional)

They must be set inside the environment PATH variable in order to be used by
the script.

# Usage

The project is gradle based so all the command line options described at https://developer.android.com/studio/build/building-cmdline are relevant.

E.g. to build APK use

`gradlew assembleDebug`

# Optional Steps
## Enable google maps
Follow instructions in *diveboard/diveboard/src/{debug|release}/res/values/google_maps_api.xml* and put API key there.

You can mark it then as git-unchanged `git update-index --assume-unchanged Diveboard\diveboard\src\debug\res\values\google_maps_api.xml`

## Enable crashlytics
Follow instructions at https://firebase.google.com/docs/android/setup and copy file into *diveboard/google-services.json*

# Misc
## Build release package
Run `gradle assembleBetaRelease`

## Screen designs
Screen designs are located here https://xd.adobe.com/view/d4bb134d-0606-41e1-4e79-5d55fce0792a-a28a/

If you want to build using gradle console (not within Android Studio) then set ANDROID_HOME env variable to Android SDK, e.g. C:\Users\<user>\AppData\Local\Android\sdk

# Prepare for release
1. Put key into Diveboard/src/release/res/values/google_maps_api.xml
2. Put passwords into Diveboard/keystore.properties
3. Copy upload-keystore.jks into root folder