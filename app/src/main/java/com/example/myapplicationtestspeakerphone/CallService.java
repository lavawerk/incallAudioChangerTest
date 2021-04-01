package com.example.myapplicationtestspeakerphone;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.InCallService;
import android.util.Log;

public class CallService extends InCallService {

    private static CallService sInstance;
    private static final String TAG = CallService.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Log.d(TAG, "onCreate");
    }
    public static CallService getInstance(){
        if (sInstance == null) Log.e(TAG, "sInstance is null!");
        return sInstance;
    }

    private final IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        Bundle extras = arg0.getExtras();
        Log.d(TAG,"onBind");
        return mBinder;
    }

    public class MyBinder extends Binder {
        CallService getService() {
            return CallService.this;
        }
    }
}
