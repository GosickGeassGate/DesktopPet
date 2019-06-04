package com.example.a22402.desktop_pet;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class PrefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);    // 设置返回键
        actionBar.setTitle("设置");

        FragmentManager fManager = getFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        SettingFragment fg = new SettingFragment();
        Bundle bundle = new Bundle();
        fg.setArguments(bundle);
        fTransaction.add(R.id.content, fg);
        fTransaction.commit();

        Log.d("name", SettingFragment.getName(this));
        Log.d("sex", SettingFragment.getSex(this));
        Log.d("type", SettingFragment.getType(this));
        Log.d("emotion", SettingFragment.getEmotion(this));
        Log.d("autoStart", SettingFragment.autoStartAdmit(this) + "");
        Log.d("alarmClock", SettingFragment.alarmClockAdmit(this) + "");
        Log.d("weChatNotify", SettingFragment.weChatNotifyAdmit(this) + "");
    }

    // 响应Action按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
