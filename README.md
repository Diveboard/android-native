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

# Prepare for release
[Blackbox](https://github.com/StackExchange/blackbox) encryption tool is used so all the production secrets reside in the repo in encrypted form.

>Blackbox is supported on all the OS's even on Windows with Mingw (goes with git installation). For Windows developers just copy *balckbox/bin* directory into local folder and add it into PATH. You can now use Blackbox in you git bash console.

You can be added into Blackbox admins list by any user listed in *.blackbox\blackbox-admins.txt*. See full instructions [here](https://github.com/StackExchange/blackbox#how-to-indoctrinate-a-new-user-into-the-system).

## Create release package
1. Decrypt all the encrypted files by running `blackbox_decrypt_all_files`
2. Execute `gradle assembleRelease`
3. (optionally) Execute `blackbox_shred_all_files` to remove all the decrypted files

# Misc
## Screen designs
Screen designs are located here https://xd.adobe.com/view/d4bb134d-0606-41e1-4e79-5d55fce0792a-a28a/

If you want to build using gradle console (not within Android Studio) then set ANDROID_HOME env variable to Android SDK, e.g. C:\Users\<user>\AppData\Local\Android\sdk