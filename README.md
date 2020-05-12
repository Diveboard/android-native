#################################
##   Diveboard Android Build   ##
#################################


# Prerequisites

You must ensure that the following packages are installed:
* Android SDK

They must be set inside the environment PATH variable in order to be used by
the script.

# Usage

The project is gradle based so all the command line options described at https://developer.android.com/studio/build/building-cmdline are relevant.

E.g. to build APK use

`gradlew assembleDebug`

# Optional Steps
## Enable google maps
Follow instructions in *diveboard/diveboard/src/{debug}/res/values/google_maps_api.xml* and put API key there.
## Enable crashlytics
Follow instructions at https://firebase.google.com/docs/android/setup and copy file into *diveboard/google-services.json*