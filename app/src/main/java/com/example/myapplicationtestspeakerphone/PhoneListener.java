package com.example.myapplicationtestspeakerphone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.telecom.CallAudioState;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneListener extends PhoneStateListener {

    private static final String TAG = PhoneListener.class.getName();

    Context context;
    AudioManager audioManager;
    TelephonyManager telephonyManager;
    private CallService callservice;


    public PhoneListener(Context ctx) {
        this.context = ctx;
        telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        doBindService();
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        Log.i(TAG, "onCallStateChanged : " + state);

        if (state == TelephonyManager.CALL_STATE_IDLE) {
            setVolumeToMax();
        }

        if (state == TelephonyManager.CALL_STATE_RINGING) {
            Log.i(TAG, "state : " + state);
        }

        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            Log.i(TAG, "state : " + state);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.e(TAG,e.getMessage());
            }
            //mHelper.blockAudioChange(false);
            int speaker = CallAudioState.ROUTE_SPEAKER;

            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P){

                //CallService.getInstance().setAudioRoute(speaker);
                if(callservice != null) {
                    callservice.setAudioRoute(speaker);
                    Log.d(TAG, "setAudioRoute");
                }
            } else {
                audioManager.setSpeakerphoneOn(true);
            }

            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P){
                if(callservice != null) {
                    CallAudioState currentState = callservice.getCallAudioState();
                    Log.d(TAG, "currentState is: " + currentState);
                }
                //Log.i(TAG, "getRoute() after setAudioRoute()  = " + CallService.getInstance().getRoute());
            } else {
                Log.i(TAG, "isSpeakerphoneOn() after setSpeakerphoneOn(true)  = " + audioManager.isSpeakerphoneOn());
            }

            //mHelper.blockAudioChange(true);
            setVolumeToMax();
        }
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        //Log.i(TAG, "Service state : " + serviceState.getState());
    }

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
        Log.i(TAG, "onDataConnectionStateChanged : " + state + ", " + networkType);
    }

    @Override
    public void onUserMobileDataStateChanged(boolean enabled) {
        super.onUserMobileDataStateChanged(enabled);
        Log.i(TAG, "onUserMobileDataStateChanged : " + enabled);
    }

    private void setVolumeToMax() {

        //mHelper.blockAudioChange(false);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

        while (actualVolume < maxVolume) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, 0);
            actualVolume++;
        }

        //mHelper.blockAudioChange(true);
    }

    public ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            callservice = ((CallService.MyBinder) binder).getService();
            Log.d(TAG,"ServiceConnection connected");
            //showServiceData();
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG,"ServiceConnection disconnected");
            callservice = null;
        }
    };

    public void doBindService() {
        Log.d(TAG,"ServiceConnection doBindService");
        Intent intent = new Intent(context, CallService.class);
        intent.setAction("DirectBind");

        context.bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

}
