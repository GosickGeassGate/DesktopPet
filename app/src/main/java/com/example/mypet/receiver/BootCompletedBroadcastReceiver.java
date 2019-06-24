package com.example.mypet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.mypet.activity.MainActivity;
import com.example.mypet.activity.WelcomeActivity;
import com.example.mypet.control.MyWindowManager;
import com.example.mypet.util.MyApplication;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context, "自启动成功", Toast.LENGTH_LONG).show();
        if (ACTION.equals(intent.getAction())) {
            MyWindowManager.createPetSmallWindow(MyApplication.getContext());
        }
    }
}
