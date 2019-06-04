package com.example.a22402.desktop_pet;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference namePref;
    private ListPreference sexPref;
    private ListPreference typePref;
    private ListPreference emotionPref;
    private CheckBoxPreference autoStartPref;
    private CheckBoxPreference alarmClockPref;
    private CheckBoxPreference weChatNotifyPref;

    @Override
    public void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);
        addPreferencesFromResource(R.xml.preferences);

        namePref = (EditTextPreference)findPreference("name_key");
        sexPref = (ListPreference)findPreference("sex_key");
        typePref = (ListPreference)findPreference("type_key");
        emotionPref = (ListPreference)findPreference("emotion_key");
        autoStartPref = (CheckBoxPreference)findPreference("AutoStart_key");
        alarmClockPref = (CheckBoxPreference)findPreference("AlarmClock_key");
        weChatNotifyPref = (CheckBoxPreference)findPreference("WeChatNotify_key");
    }

    @Override
    public void onResume(){
        super.onResume();
        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String []keys = new String[]{"name_key", "sex_key", "type_key", "emotion_key", "AutoStart_key", "AlarmClock_key", "WeChatNotify_key"};
        for(String key: keys){
            setSummary(sharedPreferences, key);
        }
        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        setSummary(sharedPreferences, key);
    }

    public void setSummary(SharedPreferences sharedPreferences, String key){
        switch(key){
            case "name_key":
                namePref.setSummary(sharedPreferences.getString(key, "NoName"));
                break;
            case "sex_key":
                if(sharedPreferences.getString(key, "male").equals("male"))
                    sexPref.setSummary("男");
                else
                    sexPref.setSummary("女");
                break;
            case "type_key":
                if(sharedPreferences.getString(key, "type1").equals("type1"))
                    typePref.setSummary("type1");
                break;
            case "emotion_key":
                if(sharedPreferences.getString(key, "emotion1").equals("emotion1"))
                    emotionPref.setSummary("emotion1");
                break;
            case "AutoStart_key":
                autoStartPref.setSummary(sharedPreferences.getBoolean(key, true) ? "是" : "否");
                break;
            case "AlarmClock_key":
                autoStartPref.setSummary(sharedPreferences.getBoolean(key, true) ? "是" : "否");
                break;
            case "WeChatNotify_key":
                autoStartPref.setSummary(sharedPreferences.getBoolean(key, true) ? "是" : "否");
                break;
            default:
                break;
        }
    }

    // 获取昵称
    public static String getName(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString("name_key", "NoName");
    }

    // 获取性别
    public static String getSex(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString("sex_key", "male");
    }

    // 获取类型
    public static String getType(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString("type_key", "type1");
    }

    // 获取表情
    public static String getEmotion(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString("emotion_key", "emotion1");
    }

    // 是否允许随机启动
    public static boolean autoStartAdmit(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean("AutoStart_key", true);
    }

    // 是否开启闹钟提醒
    public static boolean alarmClockAdmit(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean("AlarmClock_key", true);
    }

    // 是否接收微信通知
    public static boolean weChatNotifyAdmit(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean("WeChatNotify_key", true);
    }
}
