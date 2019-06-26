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

### 2019/6/24

### Version 2.1

#### 更新说明：

对设置属性的选项、默认值进行了更新，去除了表情emotion，而且悬浮窗宠物可以根据设置页的改动去变动昵称name、性别sex和主题theme（即类型type）。

- 外观属性
  - 昵称name：
    - 默认值：无名
  - 性别sex：
    - 男（默认值）
    - 女
  - 类型type
    - 影子（默认值）——对应的theme为themeDefault = 0
    - 辛巴——对应的theme为theme1 = 1
    - 阿狸——对应的theme为theme2 = 2
- 功能属性
  - 允许随机启动
    - 默认值为true
  - 允许闹钟提醒
    - 默认值为true
  - 允许微信通知
    - 默认值为true

#### 注意事项：

* 由于在宠物弹出对话框（MyWindowManager.java: 100-102）以及宠物变换表情（PetFreeService.java: 94&116）的时候需要先从SettingFragment读取属性信息更新Pet对象的静态成员，e.g. name, sex, theme，然后再把这些成员传递到方法中对宠物进行修改。（这种情况下Pet类显得有点鸡肋，明明可以直接将SettingFragment读取到的属性信息直接传递到方法中的，但是我也不敢直接删掉Pet类，毕竟还有Pet类里面还有其他静态成员信息）
* 由于宠物表情更新是有时间间隔限制的，所以在属性页对宠物类型的修改之后的呈现也是会有时间延迟的。

------

### version 2.0

#### 更新说明：

新增了BootCompletedBroadcastReceiver并进行相应的活动注册与权限申请，以响应开机广播BOOT_COMPLETED。

#### 主要文件分布：

java

- Package name
  - activity
  - control
  - model
  - receiver
    - BootCompletedBoardCast.java
  - service
  - util
  - view

#### 实现功能：

开机自启动宠物的悬浮窗，但是一段时间后会闪退，原因暂时未明。另外由于模拟器无法接收到BOOT_COMPLETED广播，所以需要使用真机调试，而且需要在权限设置中允许软件的开机自启动。

------

### 2019/6/5

### version 1.0（已整合2019/6/4）

#### 更新说明：

两年前的某个学校的老师布置了一模一样的作业。我无意间发现了这份源码并分享给大家。希望能够帮到大家。

https://github.com/frankliu3028/Desktopet/tree/develop

我借鉴了一些代码，因为要快点搭好框架开始分工。不过源码有很多bug，所以大家不要直接copy。合则用，不合则弃。

主页面的UI需在后期会做一些修改，避免查重。现在暂时用着。

#### 主要文件分布：
* java
  * Package name
    * activity
      * MainActivity.java（主界面，分工可以从onClick函数开始操作）
    * control
    * model
    * service
    * util
    * view

#### 实现功能：

* 基本框架
* 悬浮桌面，动效表情，自动贴边，跟随手指移动

------
### 2019/6/4 

#### 文件分布：

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

#### 实现功能：

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

#### 对外接口：

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

