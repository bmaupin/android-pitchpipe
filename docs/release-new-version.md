# Release a new version

1. Update [`app/build.gradle`](../app/build.gradle)

   1. Increment `versionCode`
   1. Increment `versionName`
   1. Commit and push

1. Build the APK

   1. In Android Studio, go to _Build_ > _Generate Signed Bundle / APK..._
   1. Select _APK_ > _Next_ > _Next_
   1. Make sure _release_ is selected > _Finish_
   1. Once the build is done, click _locate_ in the popup notifcation
      - The APK will be located in app/release/app-release.apk
   1. (Optional) Rename the APK file for clarification, e.g. `io.bmaupin.pitchpipe-0.7.0.apk`

1. Create a new release in GitHub

   - Drop the APK file from the previous step into the release to add it as a release binary
   - This will create the new Git tag, which will trigger a new build for F-Droid. It can take a few days before the new version is available in F-Droid
   - In GitHub, go to the previous tag to see the commits since the last release, which can be used to create the release notes

1. Create the app bundle

   1. In Android Studio, go to _Build_ > _Generate Signed Bundle / APK..._
   1. Select _Android App Bundle_ > _Next_ > _Next_
   1. Make sure _release_ is selected > _Finish_
   1. Once the build is done, click _locate_ in the popup notifcation
      - The APK will be located in app/release/app-release.aab

1. Create the new release in Google Play

   1. Go to [https://play.google.com/console/](https://play.google.com/console/)
   1. Under _All apps_ click the app
   1. Go to _Production_ > _Create new release_
   1. Upload the app bundle (app-release.aab) from the previous step
   1. Click _Next_
   1. Click _Add release notes_ and add the link to the GitHub release
   1. Click _Next_ > _Start rollout to Production_ > _Rollout_

1. (Optional) Add a note to any issues/pull requests noting the new release, e.g.

   > This has been released as version 0.7.0. You can grab the APK from the [Releases](https://github.com/bmaupin/android-pitchpipe/releases) page or wait for F-Droid/Google Play to update the release (typically a few days).
   >
   > Thanks!
