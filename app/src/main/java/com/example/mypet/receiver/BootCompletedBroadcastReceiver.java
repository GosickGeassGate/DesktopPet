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
import com.example.mypet.util.SettingFragment;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context, "自启动成功", Toast.LENGTH_LONG).show();
        // 收到BOOT_COMPLETED而且设置允许自启动
        if (ACTION.equals(intent.getAction()) && SettingFragment.autoStartAdmit(MyApplication.getContext())) {
            MyWindowManager.createPetSmallWindow(MyApplication.getContext());
        }
    }
}
