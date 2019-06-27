package com.example.mypet.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.a95306.clock.AlarmEntrance;
import com.example.mypet.R;
import com.example.mypet.control.MyWindowManager;
import com.example.mypet.service.MyNotifiService;


/**
 * Created by YZhH on 2019/6/5.
 */

public class MainActivity extends AppCompatActivity{

	public static final int OVERLAY_PERMISSION_REQ_CODE = 1001;

	private final int btnAnimDuration = 500;

	private ImageButton petBtn;
	private ImageButton weChatBtn;
	private ImageButton alarmBtn;
	private ImageButton bluetoothBtn;
	private ImageButton settingBtn;
	private ImageButton aboutBtn;

	private boolean petBtnState = false;
	private boolean weChatState = false;

	private Handler handler = new Handler();

	private boolean notificationIsOpen=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();		//获取控件
		initState();	//初始化状态
		setListener();	//设置监听器
	}



	private void findView(){
		petBtn = findViewById(R.id.pet_btn);
		weChatBtn = findViewById(R.id.wechat_btn);
		alarmBtn = findViewById(R.id.alarm_btn);
		bluetoothBtn = findViewById(R.id.bluetooth_btn);
		settingBtn = findViewById(R.id.settings_btn);
		aboutBtn = findViewById(R.id.about_btn);
	}

	/**
	 * 设置各控件监听器
	 */
	private void setListener(){

		petBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!petBtnState){
					if(checkFloatWindowPermission()){
						startBtnOnAnim(petBtn, R.drawable.ic_sentiment_satisfied_per100_60dp, new Runnable() {
							@Override
							public void run() {
								MyWindowManager.createPetSmallWindow(getApplicationContext());
							}
						});
						petBtnState = true;

					}else{
						requestFloatWindowPermission();
					}
				}else {
					startBtnOffAnim(petBtn, R.drawable.src_btn_pet, new Runnable() {
						@Override
						public void run() {
							MyWindowManager.removePetSmallWindow(getApplicationContext());
						}
					});
					petBtnState = false;
				}

			}
		});


		weChatBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!weChatState) {   // 是否打开，若打开了，检查权限
					if (checkNotificationPermission(MainActivity.this)) {  // 权限是否申请，没申请则申请，否则开启监听服务。
						startBtnOnAnim(weChatBtn, R.drawable.ic_message_per100_60dp, new Runnable() {
							@Override
							public void run() {
								MyNotifiService.isRunning=true;
								startService(new Intent(MainActivity.this, MyNotifiService.class));
							}
						});
						weChatState  = true;
					}
					else {  // 没有权限的情况，这时申请完权限后应该直接返回。
						requestNotificationPermission();
					}
				}
				else{
					startBtnOffAnim(weChatBtn, R.drawable.src_btn_wechat, new Runnable() {
						@Override
						public void run() {
							MyNotifiService.isRunning=false;
						}
					});
					weChatState = false;
				}
			}
		});

		alarmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlarmEntrance alarmEntrance=new AlarmEntrance();
				Intent intent=alarmEntrance.startAlarm(MainActivity.this);
				startActivity(intent);
			}
		});

		bluetoothBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, BlueToothActivity.class);
				startActivity(intent);
			}
		});

		settingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, PrefActivity.class);
				startActivity(intent);
			}
		});

		aboutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});
	}

	private void startBtnOnAnim(final ImageButton button, final int srcNewId, final Runnable animEndRun){
		AnimatorSet animatorSet=new AnimatorSet();
		Drawable bgNew = getResources().getDrawable(R.drawable.btn_bg_pressed);
		bgNew.setAlpha(30);
		button.setBackground(bgNew);
		ObjectAnimator btnAnim=ObjectAnimator.ofFloat(button,"rotationY",0,360);
		ObjectAnimator bgAnim=ObjectAnimator.ofInt(bgNew,"alpha",30,255);
		ObjectAnimator scaleXAnim=ObjectAnimator.ofFloat(button,"scaleX",1.0f,0.2f,1.0f);
		ObjectAnimator scaleYAnim=ObjectAnimator.ofFloat(button,"scaleY",1.0f,0.2f,1.0f);
		animatorSet.setDuration(btnAnimDuration);
		animatorSet.setInterpolator(new AccelerateInterpolator());
		animatorSet.playTogether(btnAnim,bgAnim,scaleXAnim,scaleYAnim);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						button.setImageResource(srcNewId);
					}
				},btnAnimDuration/2);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				animEndRun.run();
			}

			@Override
			public void onAnimationCancel(Animator animation) {	}

			@Override
			public void onAnimationRepeat(Animator animation) {	}
		});
		animatorSet.start();
	}

	private void startBtnOffAnim(final ImageButton button, final int srcNewId, final Runnable animEndRun){
		AnimatorSet animatorSet=new AnimatorSet();
		Drawable bgOld=button.getBackground();
		//Drawable srcOld=button.getDrawable();
		final Drawable bgNew=getResources().getDrawable(R.drawable.btn_bg);
		ObjectAnimator btnAnim=ObjectAnimator.ofFloat(button,"rotationY",0,360);
		ObjectAnimator bgAnim=ObjectAnimator.ofInt(bgOld,"alpha",255,30);
		//ObjectAnimator srcAnim=ObjectAnimator.ofInt(srcOld,"alpha",255,30);
		ObjectAnimator scaleXAnim=ObjectAnimator.ofFloat(button,"scaleX",1.0f,0.2f,1.0f);
		ObjectAnimator scaleYAnim=ObjectAnimator.ofFloat(button,"scaleY",1.0f,0.2f,1.0f);
		animatorSet.setDuration(btnAnimDuration);
		animatorSet.setInterpolator(new AccelerateInterpolator());
		animatorSet.playTogether(btnAnim,bgAnim,scaleXAnim,scaleYAnim);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						button.setImageResource(srcNewId);
					}
				},btnAnimDuration/2);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				animEndRun.run();
				button.setBackground(bgNew);
			}

			@Override
			public void onAnimationCancel(Animator animation) {	}

			@Override
			public void onAnimationRepeat(Animator animation) {	}
		});
		animatorSet.start();
	}

	private void initState(){
		if(MyWindowManager.isPetWindowShowing()){
			changeBtnState(petBtn,true);
		}else{
			changeBtnState(petBtn,false);
		}
	}

	private void changeBtnState(ImageButton button, boolean state){
		switch (button.getId()){
			case R.id.pet_btn:
				if(state){
					petBtn.setBackgroundResource(R.drawable.btn_bg_pressed);
					petBtn.setImageResource(R.drawable.ic_sentiment_satisfied_per100_60dp);
				}else{
					petBtn.setBackgroundResource(R.drawable.btn_bg);
					petBtn.setImageResource(R.drawable.src_btn_pet);
				}
				petBtnState = state;
				break;
			default:
				break;
		}
	}

	// 检测安卓6.0以上动态申请悬浮窗权限
	private boolean checkFloatWindowPermission(){
		if(Build.VERSION.SDK_INT >= 23) {
			if (!Settings.canDrawOverlays(this)){
				return false;
			}
		}
		return true;
	}

	private void requestFloatWindowPermission(){
		Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Toast.makeText(this, "请打开悬浮窗权限", Toast.LENGTH_SHORT).show();
		startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
	}

	private boolean checkNotificationPermission(Context context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {        //ask for permission
				notificationIsOpen = false;
				return false;
			}
			notificationIsOpen = true;
			return true;
		}
		Log.d("测试", "进不去啊老铁");
		return false;
	}

	private void requestNotificationPermission(){
		if(!notificationIsOpen){
			Toast.makeText(this, "请打开通知使用权限", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
			startActivityForResult(intent,1);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case OVERLAY_PERMISSION_REQ_CODE:
				if(Build.VERSION.SDK_INT>=23) {
					if (Settings.canDrawOverlays(this)) MyWindowManager.createPetSmallWindow(this);
					else {
						Toast.makeText(this, "悬浮窗权限未开启", Toast.LENGTH_SHORT).show();
						changeBtnState(petBtn, false);
					}
				}
				break;
			default:
				break;
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
