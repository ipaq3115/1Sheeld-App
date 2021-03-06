package com.integreight.onesheeld.shields.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.integreight.firmatabluetooth.ArduinoFirmata;
import com.integreight.onesheeld.R;
import com.integreight.onesheeld.enums.ArduinoPin;
import com.integreight.onesheeld.model.ArduinoConnectedPin;
import com.integreight.onesheeld.shields.ShieldFragmentParent;
import com.integreight.onesheeld.shields.controller.SpeakerShield;
import com.integreight.onesheeld.shields.controller.SpeakerShield.SpeakerEventHandler;
import com.integreight.onesheeld.utils.ConnectingPinsView;
import com.integreight.onesheeld.utils.ConnectingPinsView.OnPinSelectionListener;
import com.integreight.onesheeld.utils.Log;

public class BuzzerFragment extends ShieldFragmentParent<BuzzerFragment> {
    private final int[] levelsResources = new int[]{
            R.drawable.buzzer_shield_0_volume,
            R.drawable.buzzer_shield_25_volume,
            R.drawable.buzzer_shield_50_volume,
            R.drawable.buzzer_shield_75_volume,
            R.drawable.buzzer_shield_100_volume};
    private ImageView buzzerSpeaker;
    private Button increaseBtn, decreaseBtn;
    private int currLevel = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.buzzer_shield_fragment_layout, container,
                false);
        return v;

    }

    @Override
    public void onStart() {

        if (getApplication().getRunningShields().get(getControllerTag()) == null) {
            if (!reInitController())
                return;
        }
        ConnectingPinsView.getInstance().reset(
                getApplication().getRunningShields().get(getControllerTag()),
                new OnPinSelectionListener() {

                    @Override
                    public void onSelect(ArduinoPin pin) {
                        if (pin != null) {
                            ((SpeakerShield) getApplication()
                                    .getRunningShields()
                                    .get(getControllerTag()))
                                    .setConnected(new ArduinoConnectedPin(
                                            pin.microHardwarePin,
                                            ArduinoFirmata.INPUT));
                        } else {
                            ((SpeakerShield) getApplication()
                                    .getRunningShields()
                                    .get(getControllerTag())).connectedPin = -1;
                            // toggleLed(false);
                        }

                    }

                    @Override
                    public void onUnSelect(ArduinoPin pin) {
                        ((SpeakerShield) getApplication().getRunningShields()
                                .get(getControllerTag())).stopBuzzer();
                    }
                });
        buzzerSpeaker = (ImageView) v
                .findViewById(R.id.speaker_shield_imageview);
        buzzerSpeaker
                .setBackgroundResource(getBuzzerVolumeResource(((SpeakerShield) getApplication()
                        .getRunningShields().get(getControllerTag()))
                        .getBuzzerVolume()));
        increaseBtn = (Button) v.findViewById(R.id.increaseBtn);
        decreaseBtn = (Button) v.findViewById(R.id.decreaseBtn);
        increaseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int buzVol = ((SpeakerShield) getApplication()
                        .getRunningShields().get(getControllerTag()))
                        .getBuzzerVolume();
                if (buzVol < 100) {
                    buzVol = buzVol + 25;
                    ((SpeakerShield) getApplication().getRunningShields().get(
                            getControllerTag())).setBuzzerVolume(buzVol);
                    buzzerSpeaker
                            .setBackgroundResource(getBuzzerVolumeResource(((SpeakerShield) getApplication()
                                    .getRunningShields()
                                    .get(getControllerTag())).getBuzzerVolume()));
                }
            }
        });
        decreaseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int buzVol = ((SpeakerShield) getApplication()
                        .getRunningShields().get(getControllerTag()))
                        .getBuzzerVolume();
                if (buzVol > 0) {
                    buzVol = buzVol - 25;
                    ((SpeakerShield) getApplication().getRunningShields().get(
                            getControllerTag())).setBuzzerVolume(buzVol);
                    buzzerSpeaker
                            .setBackgroundResource(getBuzzerVolumeResource(((SpeakerShield) getApplication()
                                    .getRunningShields()
                                    .get(getControllerTag())).getBuzzerVolume()));
                }
            }
        });
        super.onStart();
    }

    private int getBuzzerVolumeResource(float volume) {
        currLevel = volume == 0 ? 0 : volume == 25 ? 1 : volume == 50 ? 2
                : volume == 75 ? 3 : volume == 100 ? 4 : currLevel;
        return levelsResources[currLevel];
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("BuzeerFragment::OnActivityCreated()", "");

    }

    private SpeakerEventHandler speakerEventHandler = new SpeakerEventHandler() {

        @Override
        public void onSpeakerChange(boolean isOn) {

            if (canChangeUI()) {
            }

        }
    };

    private void initializeFirmata() {
        if (getApplication().getRunningShields().get(getControllerTag()) == null) {
            getApplication().getRunningShields().put(getControllerTag(),
                    new SpeakerShield(activity, getControllerTag()));
        }

    }

    @Override
    public void doOnServiceConnected() {
        initializeFirmata();
    }

    @Override
    public void onResume() {
        ((SpeakerShield) getApplication().getRunningShields().get(
                getControllerTag()))
                .setSpeakerEventHandler(speakerEventHandler);
        ((SpeakerShield) getApplication().getRunningShields().get(
                getControllerTag())).doOnResume();
        super.onResume();
    }
}
