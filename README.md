For being able to turn on speaker during call, android:sharedUserId="android.uid.phone", needs to be set in the application AndroidManifest.xml.

this then requires the application to be signed with platform keys, to avoid below error:
  The application could not be installed: INSTALL_FAILED_SHARED_USER_INCOMPATIBLE
