# DesktopPet

### 分工

#### 杨仲恒

- 悬浮桌面（15week），动效表情，模型选择
  - 悬浮：Service+WindowManager
- 自动贴边，跟随手指移动
  - 拖拽：TouchListener

#### 姚振杰

- 定义属性（15week）
- 随机启动
  - BroadcastReceiver

#### 叶梓豪

- 闹钟提醒
  - AlarmManager+BroadcastReceiver

#### 杨滨好

- 蓝牙配对
  - BlueAdapter+BroadcastReceiver

#### 詹建洲

- 微信通知
  - NotificationListenerService

<!--其余未注明时间的工作尽量在16week前完成-->



-------

### 版本

compileSDK：27

minSDK：23



------

### 进度

#### 2019/6/4 姚振杰

##### 文件分布：

* java
  * Package name
    * PrefActivity.java
    * SettingFragment.java

* res
  * layout
    * activity_pref.xml
  * values
    * arrays.xml
  * xml
    * preferences.xml

##### 实现功能：

* 设置属性：
  * 外观属性
    - 昵称name
    - 性别sex
    - 类型type
    - 表情emotion
  * 功能属性
    - 允许随机启动
    - 允许闹钟提醒
    - 允许微信通知

##### 对外接口：

```Java
// 获取昵称
public static String SettingFragment::getName(Context mContext);
// 获取性别
public static String SettingFragment::getSex(Context mContext);
// 获取类型(暂定只有一个选项：type1，要等具体模型建立好再细化)
public static String SettingFragment::getType(Context mContext);
// 获取表情(暂定只有一个选项：emotion1，要等具体模型建立好再细化)
public static String SettingFragment::getEmotion(Context mContext);
// 是否允许随机启动
public static boolean SettingFragment::autoStartAdmit(Context mContext);
// 是否开启闹钟提醒
public static boolean SettingFragment::alarmClockAdmit(Context mContext);
// 是否接收微信通知    
public static boolean SettingFragment::weChatNotifyAdmit(Context mContext);
```

