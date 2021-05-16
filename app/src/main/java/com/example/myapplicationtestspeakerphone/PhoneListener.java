package com.example.myapplicationtestspeakerphone;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneListener extends PhoneStateListener {

    private static final String TAG = PhoneListener.class.getName();

    Context context;
    AudioManager audioManager;
    TelephonyManager telephonyManager;

    public PhoneListener(Context ctx) {
        this.context = ctx;
        telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
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
            audioManager.setSpeakerphoneOn(true);
            Log.i(TAG, "isSpeakerphoneOn() after setSpeakerphoneOn(true)  = " + audioManager.isSpeakerphoneOn());
            setVolumeToMax();
        }
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
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

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

        while (actualVolume < maxVolume) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, 0);
            actualVolume++;
        }
    }
}
