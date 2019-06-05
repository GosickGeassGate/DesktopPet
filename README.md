# DesktopPet

### 分工

#### 

- 悬浮桌面（15week），动效表情，模型选择
  - 悬浮：Service+WindowManager
- 自动贴边，跟随手指移动
  - 拖拽：TouchListener

#### 

- 定义属性（15week）
- 随机启动
  - BroadcastReceiver

#### 

- 闹钟提醒
  - AlarmManager+BroadcastReceiver

#### 

- 蓝牙配对
  - BlueAdapter+BroadcastReceiver

#### 

- 微信通知
  - NotificationListenerService

<!--其余未注明时间的工作尽量在16week前完成-->



-------

### 版本

compileSDK：26或以上

minSDK：24



------

### 进度

#### 2019/6/5

##### 更新说明：

两年前的某个学校的老师布置了一模一样的作业。我无意间发现了这份源码并分享给大家。希望能够帮到大家。

https://github.com/frankliu3028/Desktopet/tree/develop

我借鉴了一些代码，因为要快点搭好框架开始分工。不过源码有很多bug，所以大家不要直接copy。合则用，不合则弃。

主页面的UI需在后期会做一些修改，避免查重。现在暂时用着。

##### 主要文件分布：
* java
  * Package name
    * activity
      * MainActivity.java（主界面，分工可以从onClick函数开始操作）
    * control
    * model
    * service
    * util
    * view

##### 实现功能：

* 基本框架

* 悬浮桌面，动效表情，自动贴边，跟随手指移动


#### 2019/6/4 

##### 文件分布：

* AndroidManifest.xml（修改）

* java
  * Package name
    * PrefActivity.java（新增）
    * SettingFragment.java（新增）

* res
  * layout
    * activity_pref.xml（新增）
  * values
    * arrays.xml（新增）
  * xml
    * preferences.xml（新增）

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

