package com.example.mypet.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mypet.R;
import com.example.mypet.util.SettingFragment;

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
	}

	// 响应Action按钮的点击事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Toast.makeText(this, "宠物设置已保存", Toast.LENGTH_SHORT).show();
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
