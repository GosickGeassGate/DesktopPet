package com.example.mypet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.mypet.R;

/**
 * Created by BiaYao on 2019/6/4.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference namePref;
    private ListPreference sexPref;
    private ListPreference typePref;
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
        autoStartPref = (CheckBoxPreference)findPreference("AutoStart_key");
    }

    @Override
    public void onResume(){
        super.onResume();
        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String[]keys = new String[]{"name_key", "sex_key", "type_key", "AutoStart_key"};
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
                namePref.setSummary(sharedPreferences.getString(key, "无名"));
                break;
            case "sex_key":
                sexPref.setSummary(sharedPreferences.getString(key, "男"));
                break;
            case "type_key":
                typePref.setSummary(sharedPreferences.getString(key, "影子"));
                break;
            case "AutoStart_key":
                autoStartPref.setSummary(sharedPreferences.getBoolean(key, true) ? "是" : "否");
                break;
            default:
                break;
        }
    }

    // 获取昵称
    public static String getName(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString("name_key", "无名");
    }

    // 获取性别
    public static String getSex(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString("sex_key", "男");
    }

    // 获取主题（根据宠物类型）
    public static int getTheme(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        switch (sharedPreferences.getString("type_key", "影子")){
            case "影子":
                return 0;
            case "辛巴":
                return 1;
            case "阿狸":
                return 2;
            default:
                return 0;
        }
    }

    // 是否允许随机启动
    public static boolean autoStartAdmit(Context mContext){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean("AutoStart_key", true);
    }

}
