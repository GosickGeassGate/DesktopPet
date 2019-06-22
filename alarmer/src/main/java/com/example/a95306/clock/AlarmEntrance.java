package com.example.a95306.clock;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 95306 on 2019-06-16.
 */

public class AlarmEntrance  extends  AppCompatActivity {
    public  static  String ACTION="com.yzh.alarm";
    public  Intent startAlarm(Context mContext){
        Intent intent=new Intent(mContext,ClockActivity.class);
        return intent;
    }
}
