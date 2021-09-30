For being able to turn on speaker during call, android:sharedUserId="android.uid.phone", needs to be set in the application AndroidManifest.xml.

this then requires the application to be signed with platform keys, to avoid below error:
  The application could not be installed: INSTALL_FAILED_SHARED_USER_INCOMPATIBLE


the reason for this issue is hte permission check in fwb/services/core/java/com/android/server/audio/AudioService.java

        if (mContext.checkCallingOrSelfPermission(
                android.Manifest.permission.MODIFY_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            synchronized (mSetModeDeathHandlers) {
                for (SetModeDeathHandler h : mSetModeDeathHandlers) {
                    if (h.getMode() == AudioSystem.MODE_IN_CALL) {
                        Log.w(TAG, "getMode is call, Permission Denial: setSpeakerphoneOn from pid="
                                + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
                        return;
                    }
                }
            }
        }
  
  this leads to needing 'android.Manifest.permission.MODIFY_PHONE_STATE', which can't be aquired by third-party apps. Only way around this, and hence just a minor change to the above solution, is to request this permission by manifest entry:
      <uses-permission android:name="android.Manifest.permission.MODIFY_PHONE_STATE" />
and then also sign the apk with platform keys
