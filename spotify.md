harmonize spotify workflow


1) Download and unzip: https://github.com/spotify/android-auth/releases

2) Build
	a) get the path to android sdk location: Android Studio --> Tools --> SDK Manager --> Android SDK Location
	b) set environment variable ANDROID_HOME to this path
	c) Ensure SDK 24 is completely installed
		i) Android Studio --> Tools --> SDK Manager --> SDK Platforms --> Check Show Package Details --> Check Android 7.0 (Nougat) and Support Repository/ConstraintLayout for Android and all boxes for Solver for ConstraintLayout --> Apply --> Okay
	d) cd into unzipped directory
	e) in `auth-lib/build.gradle` and `auth-sample/build.gradle`, add `'abortOnError false'` to `android/lintOptions`
	f) `./gradlew build`


3) Copy the auth-lib directory into the /app/libs directory in your project’s root directory.
or 3) Copy the `auth-lib/build/outputs/aar/spotify-auth-version.aar` file into the /app/libs directory in your project’s root directory if just using Streaming functionality.

4) Gradle Sync

5) Use an emulator or target device with Google Play

6) Install spotify on emulator or target device

7) Navigate through app or modify the manifest to open the platform connect page. Select Spotify and log in. A toast should show the access token given by the api response and Logcat should should show an ApiPlaylistData object.

NOTES:
- `api 'com.spotify.android:auth:1.0.0-alpha'` added to build.gradle(Module: app) dependencies
- INTERNET permission added to manifest
- spotify login activity added to manifest
- `SpotifyClient` is just a wrapper for all api calls/http requests
- `ApiPlaylistData` is a class that the entire playlist json response gets mapped to. ApiPlaylist just maps a single playlist in the "items" element of the response. Preferably, fewer classes would be needed, but I haven't found a way to decrease the composition needed while using Klaxxon. Also, these names will change