package com.project.agrostore.taskmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.project.agrostore.taskmanager.MusicControl;

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("com.agrostore.SnoozeReceiver")) {
            MusicControl.getInstance(context).stopMusic();
        }
    }
}
