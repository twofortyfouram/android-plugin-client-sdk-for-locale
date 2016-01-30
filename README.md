[![Build Status](https://travis-ci.org/twofortyfouram/android-plugin-client-sdk-for-locale.png?branch=master)](https://travis-ci.org/twofortyfouram/android-plugin-client-sdk-for-locale)

# Overview
[Locale](https://play.google.com/store/apps/details?id=com.twofortyfouram.locale) allows developers to create plug-in conditions and settings.  The android-plugin-client-sdk-for-locale implements a set of classes to simplify building a plug-in.  This SDK is the middle layer of the Locale Developer Platform.

Although there are multiple ways to approach building a plug-in host or plug-in client, we do not recommend starting with this SDK layer.  Instead we strongly recommend starting with the main [Locale Developer Platform documentation](http://www.twofortyfouram.com/developer).


# API Reference
JavaDocs for the library are published [here](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale).


# Compatibility
The library is compatible and optimized for Android API Level 8 and above.


# Download
## Gradle
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        jcenter()
    }

And the dependencies section would look something like this:

    dependencies {
        compile group:'com.twofortyfouram', name:'android-plugin-client-sdk-for-locale', version:'[4.0.2, 5.0['
    }


# Creating a Plug-in
## Fundamentals
A plug-in implementation consists of two things:

1. Activity: for the [ACTION_EDIT_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_CONDITION) or [ACTION_EDIT_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_SETTING) Intent action.
1. BroadcastReceiver: for the [ACTION_QUERY_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_QUERY_CONDITION) or [ACTION_FIRE_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_FIRE_SETTING) Intent action.

At runtime the host launches the plug-in's Activity, the plug-in's Activity returns a Bundle to the host, and the host will send that Bundle back to the plug-in's BroadcastReceiver when it is time to query/fire the plug-in.  The host may also pass the Bundle back to the Activity in the future, if the user wishes to edit the plug-in's configuration again.


## Step by Step
1. Add dependencies to build.gradle as described in the Usage section above.
1. Architect the contents of the plug-in's [EXTRA_BUNDLE](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#EXTRA_BUNDLE).  We recommend implementing a "BundleManager" object with static methods to verify the Bundle is correct, generate a new Bundle, and extract values from the Bundle.  
1. Implement the "Edit" Activity:
    1. Subclass [AbstractPluginActivity](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html) (or [AbstractFragmentPluginActivity](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractFragmentPluginActivity.html) for android-support-v4 compatibility) and provide implementations for:
        1. [isBundleValid(android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#isBundleValid(android.os.Bundle)): Determines whether a Bundle is valid.
        1. [onPostCreateWithPreviousResult(android.os.Bundle previousBundle, java.lang.String previousBlurb)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#onPostCreateWithPreviousResult(android.os.Bundle,%20java.lang.String)): If the user is editing an old instance of the plug-in, this allows the Activity to restore state from that old plug-in configuration.
        1. [getResultBundle()](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#getResultBundle()): When the Activity is finishing, this method will return the Bundle that represents the plug-in's state.  This Bundle will eventually be sent to the BroadcastReceiver when the plug-in is queried.
        1. [getResultBlurb(android.os.Bundle bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#getResultBlurb(android.os.Bundle)): When the Activity is finishing, this method will return a concise, human-readable description of the plug-in's state that may be displayed in the host's UI.
    1. Add the AndroidManifest entry for the Activity.  *Note: It is very important that plug-in conditions and settings have a stable Activity class name.  The package and class names for the edit Activity are a plug-in's public API.  If they do not remain consistent, then saved instances of the plug-in created previously will be orphaned.  For more information, see Dianne Hackborn's blog post [Things That Cannot Change](http://android-developers.blogspot.com/2011/06/things-that-cannot-change.html).  To make maintaining a stable Activity class name easier, we recommend using an activity-alias for exposing the plug-in's edit Activity.  (It is permitted for the plug-in's BroadcastReceiver class name to change.)*
        1. Add an Intent filter for [ACTION_EDIT_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_CONDITION) or [ACTION_EDIT_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_SETTING) Intent action.
        1. Add an Activity icon: This icon will be shown in the host's UI.  The ldpi version of the icon should be 27x27 pixels, the mdpi version should be 36x36 pixels, the hdpi version of the icon should be 48x48 pixels, the xhdpi version of the icon should be 72x72 pixels, and the xxhdpi version of the icon should be 108x108 pixels.  Note: THIS ICON IS SMALLER THAN THE LAUCHER ICON.  Providing a correctly scaled icon will improve performance when the host displays the plug-in's icon.
        1. Add an Activity label: The label is the name that will be displayed in the host's UI.

                <!-- This is the real Activity implementation but it is not exposed directly. -->
                <activity
                        android:name=".ui.activity.PluginActivityImpl"
                        android:exported="false"
                        android:label="@string/plugin_name"
                        android:uiOptions="splitActionBarWhenNarrow"
                        android:windowSoftInputMode="adjustResize">
                </activity>
                <!-- This is the activity-alias, which the host perceives as being the plug-in's Edit Activity.
                     This layer of indirection helps ensure the public API for the plug-in is stable.  -->
                <activity-alias
                        android:name=".ui.activity.PluginActivity"
                        android:exported="true"
                        android:icon="@drawable/ic_plugin"
                        android:label="@string/plugin_name"
                        android:targetActivity=".ui.activity.PluginActivityImpl">
                    <intent-filter>
                        <!-- For a plug-in setting, use EDIT_SETTING instead. -->
                        <action android:name="com.twofortyfouram.locale.intent.action.EDIT_CONDITION"/>
                    </intent-filter>
                </activity-alias>
1. Implement the BroadcastReceiver:
    * Condition: Subclass [AbstractPluginConditionReceiver](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html) and provide implementations for:
        1. [isBundleValid(android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#isBundleValid(android.os.Bundle)): 
        1. [isAsync()](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#isAsync()): Determines whether the [getPluginConditionResult(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#getPluginConditionResult(android.content.Context,%20android.os.Bundle)) method should be executed in a background thread.
        1. [getPluginConditionResult(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#getPluginConditionResult(android.content.Context,%20android.os.Bundle)): Determines the state of the plug-in, which can be [RESULT_CONDITION_SATISFIED](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#RESULT_CONDITION_SATISFIED), [RESULT_CONDITION_UNSATISFIED](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#RESULT_CONDITION_UNSATISFIED), or [RESULT_CONDITION_UNKNOWN](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#RESULT_CONDITION_UNKNOWN).
    * Setting: Subclass [AbstractPluginSettingReceiver](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html) and provide implementations for:
        1. [isBundleValid(android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#isBundleValid(android.os.Bundle)): 
        1. [isAsync()](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#isAsync()): Determines whether the [firePluginSetting(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#firePluginSetting(android.content.Context,%20android.os.Bundle)) method should be executed in a background thread.
        1. [firePluginSetting(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#firePluginSetting(android.content.Context,%20android.os.Bundle)): Performs the plug-in setting's action.
1. Add the AndroidManifest entry for the BroadcastReceiver
    * Condition: Register the BroadcastReceiver with an Intent-filter for [ACTION_QUERY_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_QUERY_CONDITION):

                <receiver
                        android:name=".receiver.PluginConditionReceiverImpl"
                        android:exported="true">
                    <intent-filter>
                        <action android:name="com.twofortyfouram.locale.intent.action.QUERY_CONDITION"/>
                    </intent-filter>
                </receiver>
    * Setting: Register the BroadcastReceiver with an Intent-filter for [ACTION_FIRE_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_FIRE_SETTING):

                <receiver
                        android:name=".receiver.PluginSettingReceiverImpl"
                        android:exported="true">
                    <intent-filter>
                        <action android:name="com.twofortyfouram.locale.intent.action.FIRE_SETTING"/>
                    </intent-filter>
                </receiver>


# History
* 1.0.0: Initial release
* 1.0.1: Fix diffing of plug-in edits.  Thanks @jkane001 for reporting this issue!
* 1.1.0: Support for Material Design and appcompat-v7
* 2.0.0: Update spackleLib dependency to 2.0.0
* 3.0.0
    * Remove AbstractLocalePluginActivity and AbstractLocaleFragmentPluginActivity.  These deprecated Activities implemented UI logic, while this SDK should only responsible for communicating with the host.
    * Rename AbstractPluginConditionReceiver.getPluginConditionState(Context, Bundle) to be more internally consistent.
* 4.0.0: Remove strings and resources that were previously used by AbstractLocalePluginActivity and AbstractLocaleFragmentPluginActivity.
* 4.0.1: Fix visibility of Activity onPostCreate().  Thanks @ddykhoff for reporting this issue!
* 4.0.2: Fix async plug-in settings.  Thanks @giech for reporting this issue!