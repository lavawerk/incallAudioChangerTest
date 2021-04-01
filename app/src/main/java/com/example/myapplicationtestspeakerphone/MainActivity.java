package com.example.myapplicationtestspeakerphone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private Button buttonMakeCall,buttonTurnSpeakerphoneOn;
    private PhoneListener phoneListener;
    private TelephonyManager telephonyManager;
    private AudioManager audioManager;
    private String internationalPhoneNumber;
    private EditText editTextPhoneNumber;
    private Button infoButton;
    private static final String TAG = MainActivity.class.getName();
    private CallService callservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonMakeCall = findViewById(R.id.buttonMakeCall);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        buttonTurnSpeakerphoneOn = findViewById(R.id.buttonTurnSpeakerphoneOn);
        infoButton = findViewById(R.id.mode_info_button);
        callservice = new CallService();

        phoneListener = new PhoneListener(this);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,
                PhoneStateListener.LISTEN_CALL_STATE | PhoneStateListener.LISTEN_SERVICE_STATE
                        | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
        }

        updateUi();

        buttonMakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });

        buttonTurnSpeakerphoneOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSpeakerphoneOn();
            }
        });
    }
    //exemple format number accepted +33611223344
    private String getInternationalPhoneNumber(){
        if(editTextPhoneNumber!= null){
            if(editTextPhoneNumber.getText()!= null ){
                 internationalPhoneNumber = editTextPhoneNumber.getText().toString();
            }
        }
         return internationalPhoneNumber;
    }

    public void call() {

        setSpeakerphoneOn();
        try {
            sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" +getInternationalPhoneNumber()));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Missing call permission", Toast.LENGTH_SHORT).show();
            return;
        }
        this.startActivity(callIntent);
    }

    public void setSpeakerphoneOn() {
        Log.i(TAG, "isSpeakerphoneOn() before setSpeakerphoneOn(true)  = " + audioManager.isSpeakerphoneOn());
        audioManager.setSpeakerphoneOn(true);
        Log.i(TAG, "isSpeakerphoneOn() after setSpeakerphoneOn(true)  = " + audioManager.isSpeakerphoneOn());
        updateUi();
    }

    private void updateUi() {
        if (audioManager.isSpeakerphoneOn()) {
            buttonTurnSpeakerphoneOn.setBackgroundColor(Color.GREEN);
        } else {
            buttonTurnSpeakerphoneOn.setBackgroundColor(Color.RED);
        }
        if (infoButton != null) infoButton.setText("current state:" + callservice.getCallAudioState());
        else Log.e(TAG, "infoText is null");
    }

    @Override
    protected void onDestroy() {
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        updateUi();
        super.onResume();
    }

}