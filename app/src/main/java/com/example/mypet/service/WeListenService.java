package com.example.mypet.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.mypet.control.MyWindowManager;
import com.example.mypet.control.PetControl;

import java.util.List;


public class WeListenService extends AccessibilityService {
    public static boolean isRunning=false;
    private static final String TAG = "微信监控 ";
    private static final String PACKAGE="com.tencent.mm"; // 微信的包名

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isRunning)
            return;
        int eventType = event.getEventType();
        Log.w(TAG, "onAccessibilityEvent: " + eventType);
        if (event.getPackageName().equals(PACKAGE)) { // 只过滤微信消息
            switch (eventType) {
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    Log.w(TAG,"监听到微信消息");
                    handleNotification(event);
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    String className = event.getClassName().toString();
                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                        Log.d(TAG, "捕获到微信界面");
                    } else {
                    }
            }
        }
    }


    /**
     * 处理通知栏信息
     * <p>
     * 如果是微信红包的提示信息,则模拟点击
     *
     * @param event
     */
    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                Log.d("我是微信消息", "handleNotification:    " + content);
                if(MyWindowManager.isPetWindowShowing()){
                    PetControl.displayPetMessage(""+content);
                }
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                if (content.contains("[微信红包]")) {
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            Log.d(TAG,"accessibilityNodeInfo is null");
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    private void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        Log.d(TAG,"performViewClick start.");
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d(TAG,"has clicked the view");
                nodeInfo.recycle(); //  当点击完后回收
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }


    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "用户开启辅助功能会回调该函数");
        super.onServiceConnected();
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "服务断开时回调");
    }

}
