package com.example.mypet.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypet.R;
import com.example.mypet.control.MyWindowManager;
import com.example.mypet.model.Pet;
import com.example.mypet.util.BlueToothConn;
import com.example.mypet.util.MyApplication;
import com.example.mypet.util.SettingFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.R.id.message;

/**
 * Created by Babsis on 2019/6/6.
 */
// 当用户点击宠物的蓝牙配对功能时，就会根据提示打开蓝牙并且主动搜索其他的蓝牙设备，两个设备的宠物就可以进行通信；
// 计划的蓝牙配对还包含可以在一个界面内显示配对成功后对方的宠物。
// 功能需求：1.配对信息显示 & 配对宠物出现在窗口
//          2.发送 & 接受信息 新增聊天选项
//          3.选择不同的连接对象

//启动接口：框架MainActivity(112行)
    /*bluetoothBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    });*/
//需要其他模块的接口：得到宠物的属性（昵称、性别、类型、表情）

public class BlueToothActivity extends AppCompatActivity {
    //宠物属性
    private String selfName;
    private String selfSex;
    private int selfTheme;
    private String selfState;

    // 蓝牙资源
    private BluetoothAdapter mBluetoothAdapter = null;
    private BlueToothConn mChatService = null;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    //试图资源
    private Toolbar mToolbar;
    private Switch BlueToothopen;
    private Context context = this;
    private ListView AvailableDevice;
    private Button finding;
    private Button chatting;
    private TextView connected;
    private BroadcastReceiver mReceiver;
    //private Handler mHandler;
    private ArrayAdapter<String> Devices;

    // 定义蓝牙打开交互事件
    boolean is_selected_device = false;
    String selected_device;
    Handler timeOutHandler = new Handler();
    Runnable timeOutToast = new Runnable() {
        @Override
        public void run()
        {
            Toast.makeText(BlueToothActivity.this, "连接超时（请连接双方在app中打开蓝牙）或该设备无宠物app", Toast.LENGTH_SHORT).show();
            selected_device = null;
            Devices.clear();
        }
    };

    //互动对话框
    private AlertDialog alertDialog1 = null;
    private AlertDialog.Builder dialogBuilder1 = null;
    boolean visited = false;

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

    // 定义蓝牙会话
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //case Constants.MESSAGE_STATE_CHANGE:
                case 1:
                    switch (msg.arg1) {
                        case BlueToothConn.STATE_CONNECTED:
                            //device_view.setEnabled(true);
                            if(mBluetoothAdapter.isDiscovering())
                                mBluetoothAdapter.cancelDiscovery();
                            //deviceAdapter.clear();
                            if(is_selected_device) {
                                //deviceAdapter.add(selected_device);
                                is_selected_device = false;
                                mChatService.write(("device&&&" + mBluetoothAdapter.getName()
                                        + "\n" + mBluetoothAdapter.getAddress()).getBytes());
                            }
                            Toast.makeText(BlueToothActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
                            is_selected_device =  true;

                            timeOutHandler.removeCallbacks(timeOutToast);
                            break;
                        case BlueToothConn.STATE_CONNECTING:
                            Log.v("MyDebug", "连接中...");
                            //scan_status.setText("连接中...");
                            break;
                    }
                    break;
                //case Constants.MESSAGE_WRITE:
                case 3:

                    break;
                //case Constants.MESSAGE_READ:
                case 2:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readMessage.contains("&&&")) {
                        String[] tmp_s = readMessage.split("&&&");
                        String tag = tmp_s[0];
                        String data = tmp_s[1];
                        if (tag.equals("device")) {
                            //deviceAdapter.clear();
                            //deviceAdapter.add(data);
                            selected_device = data;
                        }else if(tag.equals("scan_click")){
                            selected_device = null;
                            //deviceAdapter.clear();
                            //anim.setEnabled(false);
                        }
                    }
                    else {
                        int friendTheme;
                        String friendSex;
                        String friendName;
                        String action = readMessage.substring(0, 2);
                        String petInfo = readMessage.substring(2);
                        String[] array = petInfo.split(" && ");
                        //selfType+" && "+selfSex + " && " + selfName
                        //String petSkin = array[0];
                        //debug: java.lang.ArrayIndexOutOfBoundsException: length=1; index=1
                        //注意不是只有拜访的时候需要发送宠物信息 其他时候是不需要的
                        Log.d("readMsg",readMessage.toString());
                        Log.e("array",array.toString());
                        String petName = "testing";
                        switch (action) {
                            case "拜访":
                                //告诉对方拜访成功 从被拜访方发出到拜访方
                                mChatService.write("拜成".getBytes());
                                //告诉悬浮窗需要显示对方宠物
                                Intent visitIntent = new Intent();
                                //visitIntent.putExtra("VisitPetSkin", petSkin);
                                //visitIntent.putExtra("VisitPetName", petName);
                                //visitIntent.setAction("com.bluetooth.visit");
                                //LocalBroadcastManager.getInstance(BlueToothActivity.this).sendBroadcast(visitIntent);
                                //label:根据获得的petType petSex petName信息对拜访宠物进行悬浮显示

                                Pet.friendtheme = Integer.parseInt(array[0]);
                                Pet.friendsex = array[1];
                                Pet.friendname = array[2];

                                Log.d("visit", "拜访朋友");
                                MyWindowManager.createFriendPetSmallWindow(MyApplication.getContext());
                                visited = true;

                                //根据获得的Type sex name创建新的宠物 进行显示
                                final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(BlueToothActivity.this);
                                alertDialog1.setTitle("收到拜访消息提示");
                                alertDialog1.setMessage("宠物" + Pet.friendname + "过来玩啦~");
                                alertDialog1.setPositiveButton("确定", null);

                                final AlertDialog dlg1 = alertDialog1.create();
                                dlg1.show();

                                final Timer t1 = new Timer();
                                t1.schedule(new TimerTask() {
                                    public void run() {
                                        dlg1.dismiss(); // when the task active then close the dialog
                                        t1.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                    }
                                }, 2000);
                                break;
                            case "召回":
                                //告诉对方召回成功 从被拜访方发出到拜访方
                                mChatService.write("召成".getBytes());
                                //告诉悬浮窗需要移除对方宠物
                                //label:根据获得的信息移除拜访宠物
                                /**/

                                MyWindowManager.removeFriendPetSmallWindow(MyApplication.getContext());
                                visited = true;
                                final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(BlueToothActivity.this);
                                alertDialog2.setTitle("收到召回消息提示");
                                alertDialog2.setMessage("宠物" + Pet.friendname + "要走啦~");
                                alertDialog2.setPositiveButton("确定", null);

                                final AlertDialog dlg2 = alertDialog2.create();
                                dlg2.show();

                                final Timer t2 = new Timer();
                                t2.schedule(new TimerTask() {
                                    public void run() {
                                        dlg2.dismiss(); // when the task active then close the dialog
                                        t2.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                    }
                                }, 2000);
                                break;

                            case "拜成":
                                if(selfState == "LEAVING") {
                                    selfState = "ARRIVED";
                                    //label:自己页面上移除自己的宠物
                                    //发一个广播移除自己的宠物
                                    /*
                                    //String MyPetName = sharedPreferences.getString("currentPet", "");
                                    Intent visitSuccessintent = new Intent();
                                    visitSuccessintent.setAction("com.bluetooth.selfback");
                                    LocalBroadcastManager.getInstance(BlueToothActivity.this).sendBroadcast(visitSuccessintent);*/

                                    final AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(BlueToothActivity.this);
                                    alertDialog3.setTitle("拜访成功消息提示");
                                    alertDialog3.setMessage("您的宠物"+selfName+"出去玩啦~");
                                    alertDialog3.setPositiveButton("确定", null);

                                    final AlertDialog dlg3 = alertDialog3.create();
                                    dlg3.show();

                                    final Timer t3 = new Timer();
                                    t3.schedule(new TimerTask() {
                                        public void run() {
                                            dlg3.dismiss(); // when the task active then close the dialog
                                            t3.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                        }
                                    }, 2000);

                                }
                                break;
                            case "召成":
                                if(selfState == "ARRIVED") {
                                    selfState = "HOME";
                                    //label:宠物回家 显示自己的宠物

                                    final AlertDialog.Builder alertDialog4 = new AlertDialog.Builder(BlueToothActivity.this);
                                    alertDialog4.setTitle("召回成功消息提示");
                                    alertDialog4.setMessage("您的宠物"+selfName+"已经回来啦~");
                                    alertDialog4.setPositiveButton("确定", null);;

                                    final AlertDialog dlg4 = alertDialog4.create();
                                    dlg4.show();

                                    final Timer t4 = new Timer();
                                    t4.schedule(new TimerTask() {
                                        public void run() {
                                            dlg4.dismiss(); // when the task active then close the dialog
                                            t4.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                        }
                                    }, 2000);
                                }
                                break;
                        }
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        Devices = new ArrayAdapter<String>(this, R.layout.device_name);
        //connected = (TextView) findViewById(R.id.connectedDevice);
        finding = (Button) findViewById(R.id.find);
        chatting = (Button) findViewById(R.id.chat);
        AvailableDevice = (ListView)findViewById(R.id.friends);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*label1 : 属性获取 合并之后修改
        */

        Pet.name = SettingFragment.getName(MyApplication.getContext());
        Pet.sex = SettingFragment.getSex(MyApplication.getContext());
        Pet.theme = SettingFragment.getTheme(MyApplication.getContext());
        selfName = Pet.name;
        selfSex = Pet.sex;
        selfTheme = Pet.theme;
        selfState = "HOME";
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        mToolbar.setTitle("DesktopPet蓝牙设置");
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();//返回
//            }
//        });

        BlueToothopen = (Switch) findViewById(R.id.blueswitch);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled())
        {
            BlueToothopen.setChecked(true);
            mChatService = new BlueToothConn(BlueToothActivity.this, mHandler);
            mChatService.start();
        }

        BlueToothopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BlueToothopen.isChecked()) {
                    Toast.makeText(BlueToothActivity.this, "打开了蓝牙", Toast.LENGTH_SHORT).show();
                    work();
                } else {
                    Toast.makeText(BlueToothActivity.this, "关闭了蓝牙", Toast.LENGTH_SHORT).show();
                    turnOff();
                }
            }
        });
        finding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BlueToothopen.isChecked()) //蓝牙设备没有打开的话先打开
                {
                    work();
                }
                if(mBluetoothAdapter.isEnabled())
                    BlueToothopen.setChecked(true);
                Toast.makeText(context, "搜索中......", Toast.LENGTH_SHORT).show();
                findingPets();
            }
        });
        chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(!BlueToothopen.isChecked()) 蓝牙设备没有打开的话先打开
                {
                    work();
                }*/
                if(!mBluetoothAdapter.isEnabled() || mChatService ==null || mChatService != null && mChatService.getState()!=3)
                    is_selected_device = false;
                interract();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (visited) {
                        if (!mBluetoothAdapter.isEnabled() || mChatService == null || mChatService != null && mChatService.getState() != 3) {
                            MyWindowManager.removeFriendPetSmallWindow(MyApplication.getContext());
                            visited = true;
                        }
                    }
                }
            }
        }).start();

    }
    void turnOff() {
        this.Devices.clear();
        mBluetoothAdapter.disable();
    }

    //Testing:打开蓝牙功能测试 checked
    void work() {
        Toast.makeText(this.context, "启动蓝牙", Toast.LENGTH_SHORT).show();
        /* private void initBluetoothConfig(){}*/
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 如果设备不支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(BlueToothActivity.this, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            BlueToothopen.setChecked(false);
            finding.setEnabled(false);
            BlueToothopen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        BlueToothopen.setChecked(false);
                        Toast.makeText(BlueToothActivity.this, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return;
        }

        //如果设备支持蓝牙
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            Toast.makeText(BlueToothActivity.this, "蓝牙设备已经打开！", Toast.LENGTH_SHORT).show();
            if(mBluetoothAdapter.isEnabled())
                BlueToothopen.setChecked(true);
            Log.d("process","1");
        }
        else{
            mChatService = new BlueToothConn(BlueToothActivity.this, mHandler);
            mChatService.start();
        }
        Log.e("debugging","p");
        // 如果设备已经打开蓝牙了
        if(mBluetoothAdapter.isEnabled()){
            BlueToothopen.setChecked(true);
            Log.e("open","searching...");
        }
    }

    void findingPets() {
        Devices.clear();
        final ArrayList<String> devices_detected = new ArrayList<String>();
        mBluetoothAdapter.startDiscovery();
        AvailableDevice.setEnabled(true);
        //获得搜索权限  Broadcast信息获取 定义结果显示 开始搜索
        Log.d("searching","start");
        // 开启搜索蓝牙的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(), ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("开启搜索其他手机的权限")
                            .setMessage("您的手机未允许该app搜索蓝牙设备，点击开启")
                            .setNeutralButton("开启搜索", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(BlueToothActivity.this,
                                                new String[]{ACCESS_COARSE_LOCATION},
                                                1);
                                    }
                                }
                            })
                            .show()
                            .findViewById(message))
                            .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    //Toast.makeText(this.context,"已有搜索权限",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        // 处理搜索蓝牙设备事务
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //fix:duplicate device names on the display
                    if(devices_detected.contains(device.getName()))
                        return ;
                    devices_detected.add(device.getName());
                    Devices.add(device.getName() + "\n" + device.getAddress());
                    Log.v("MyDebug", device.getName() + "\n" + device.getAddress());
                    // When discovery is finished, change the Activity title
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d("found","?");
                }
            }
        };
        //testing: 展示可搜索到设备 checked
        // 设置展示设备
        AvailableDevice.setAdapter(Devices);
        // 注册点击事件 与点击的设备进行连接
        AvailableDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //testing:点击设备并且进行连接通话 checked
                //测试情况:在该设备没有此宠物APP或者没有打开蓝牙的时候显示连接超时
                //Testing Point:找到另一部安卓机器之后进行连接测试
                //testing:点击设备并且进行了连接
                //Toast.makeText(BlueToothActivity.this,"连接中......",Toast.LENGTH_LONG).show();
                mBluetoothAdapter.cancelDiscovery();
                Devices.clear();
                AvailableDevice.setAdapter(Devices);
                AvailableDevice.setEnabled(false);
                TextView textView = (TextView) view;
                String text_tmp = textView.getText().toString();
                Log.d("clicking",text_tmp);
                if(selected_device != null && text_tmp.equals(selected_device)){
                    Toast.makeText(BlueToothActivity.this, "设备已连接", Toast.LENGTH_SHORT).show();
                    Log.d("selected",text_tmp);
                }
                is_selected_device = true;
                Log.d("is_selected_device_in",String.valueOf(is_selected_device));
                selected_device = text_tmp;
                String[] addresses = textView.getText().toString().split("\n");
                Log.v("MyDebug", "name and address==>>" + addresses[0] + "==>>" + addresses[1]);
                String address = addresses[1];

                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                if(!mBluetoothAdapter.isEnabled())
                {
                    Toast.makeText(BlueToothActivity.this,"蓝牙关闭了",Toast.LENGTH_LONG).show();
                    return;
                }
                if(mBluetoothAdapter.isEnabled()&&mChatService==null)
                {
                    mChatService = new BlueToothConn(BlueToothActivity.this, mHandler);
                    mChatService.start();
                }
                // Attempt to connect to the device
                mChatService.connect(device, true);
                timeOutHandler.postDelayed(timeOutToast, 10000);
            }
        });
        // 注册蓝牙搜索事件
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    //需要定义蓝牙对话
    void interract()
    {
        Log.d("is_selected_device",String.valueOf(is_selected_device));
        //is_selected_device = true;
        //testing 互动前如果没有连接好的宠物就会提示没有连接的宠物 checked
        if(!is_selected_device)
        {
            Log.e("Invalid operation","connecting pet does'nt exist");
            dialogBuilder1 = new AlertDialog.Builder(context);
            alertDialog1 = dialogBuilder1
                    .setTitle("连接错误")
                    .setMessage("没有连接的宠物")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "点击了确定按钮", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();             // 创建AlertDialog对象
        }
        //Testing:如果有可以互动的宠物 需要怎么互动
        else
        {
            //有互相连接的宠物，可以开始互动
            dialogBuilder1 = new AlertDialog.Builder(context);
            alertDialog1 = dialogBuilder1
                    .setTitle("宠物互动")
                    .setNegativeButton("探望", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "探望", Toast.LENGTH_SHORT).show();
                            //testing
                            //selfState="HOME";
                            String msg= "拜访";
                            if(selfState == "HOME") {
                                //还未加载一个loading界面
                                //petState = Constants.LEAVING;
                                selfState = "LEAVING";
                                Toast.makeText(BlueToothActivity.this,"到达朋友家",Toast.LENGTH_SHORT).show();
                                // 下面这个函数将会把消息发到另外一个蓝牙设备中, 如果需要可以在执行函数前对字符串进行处理
                                // 这里需要读取自己宠物名字
                                //String MyPetName = sharedPreferences.getString("currentName", "");
                                //String MyPetSkin = sharedPreferences.getString("current", "");
                                mChatService.write((msg + selfTheme+" && "+selfSex + " && " + selfName).getBytes());
                            }
                            else
                            {
                                Toast.makeText(BlueToothActivity.this,"您的宠物不在家",Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setPositiveButton("喊回家", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    /*发起方取消互动请求*/
                            //testing
                            //selfState = "GONE";
                            if(selfState == "ARRIVED") {
                                selfState = "HOME";
                                // 下面这个函数将会把消息发到另外一个蓝牙设备中, 如果需要可以在执行函数前对字符串进行处理
                                // 这里需要读取自己宠物的名字
                                //String MyPetName = sharedPreferences.getString("currentName", "");
                                //String MyPetSkin = sharedPreferences.getString("current", "");
                                String msg = "召回";
                                Toast.makeText(BlueToothActivity.this,"从朋友家回来",Toast.LENGTH_SHORT).show();
                                mChatService.write((msg + selfTheme+" && "+selfSex + " && " + selfName).getBytes());
                            }
                            else
                            {
                                Toast.makeText(BlueToothActivity.this,"您的宠物没有外出",Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(context, "取消互动", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();             // 创建AlertDialog对象
                    Log.d("turning to","here");
        }
        Log.d("turning to","interacting here");
        //Toast.makeText(context, "interacting", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this, "取消操作", Toast.LENGTH_SHORT).show();
            return;
        }else {
            switch (requestCode) {
                case REQUEST_ENABLE_BT: // 开启蓝牙
                    mChatService = new BlueToothConn(this, mHandler);
                    mChatService.start();
                    BlueToothopen.setChecked(true);
                    Devices.clear();
                    mBluetoothAdapter.startDiscovery();
                    break;
            }
        }
    }

    // 下面是管理声明周期的函数
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatService != null)
            mChatService.stop();
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
        if(mReceiver != null)
             this.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BlueToothConn.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }
}

