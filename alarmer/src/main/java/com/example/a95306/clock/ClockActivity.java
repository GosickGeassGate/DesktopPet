package com.example.a95306.clock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;

import java.util.HashMap;
import java.util.Map;


public class ClockActivity extends AppCompatActivity {
    private Dialog timeDialog;
    Map<Integer,Boolean> date_is_select;
    int repeat_count,ring;      //重复次数，提醒方式
    TextView tv_ring_value;
    AlertDialog alertDialog;
    LinearLayout allLayout;
    String TAG;
    String ACTION_TAG="com.loonggg.alarm.clock";
    boolean is_work;
    Context mContext;
    int[] date_id={R.id.Sun,R.id.Mon,R.id.Tue,R.id.Wed,R.id.Thu,R.id.Fri,R.id.Sat};
    String[] date= {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    int hour,minutes;
    DBOpenHandler dbOpenHandler=new DBOpenHandler(this,"Alarm",null,1);
//    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        hour=0;
        minutes=0;
        TAG=null;
        is_work=false;
        repeat_count=0;
        ring=0;
        allLayout=(LinearLayout)findViewById(R.id.activity_clock);
        tv_ring_value=(TextView) findViewById(R.id.hint_way);
        mContext=this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
//        mTextView = findViewById(R.id.textView);
        LinearLayout timeselect=(LinearLayout) findViewById(R.id.timeshow);
        timeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePick();
            }
        });
        date_is_select=new HashMap<>();
        for(int i=0;i<date_id.length;i++){
            TextView tmp=(TextView)findViewById(date_id[i]);
            date_is_select.put(date_id[i],false);
            tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(date_is_select.get(v.getId())){
                        v.setBackgroundResource(R.color.white);
                        date_is_select.put(v.getId(),false);
                    }
                    else{
                        v.setBackgroundResource(R.drawable.gray_back);
                        date_is_select.put(v.getId(),true);
                    }
                    setReaptHint();
                }
            });
        }

        RelativeLayout remind=(RelativeLayout) findViewById(R.id.remind_way);
        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRingWay();
            }
        });

        RelativeLayout tag_text=(RelativeLayout) findViewById(R.id.tag);
        tag_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTag();
            }
        });

        Switch work_switch=(Switch)findViewById(R.id.alarm_switch);
        work_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    is_work=isChecked;
            }
        });

        restart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId()==R.id.finish){
                if(TAG==null)
                    TAG="闹钟响了";
                if(is_work){
                    for(int i=0;i<8;i++){
                        AlarmManagerUtil.cancelAlarm(mContext,ACTION_TAG,i);
                    }
                    if(repeat_count==0){
                        AlarmManagerUtil.setAlarm(mContext, 1, hour, minutes, 0, 0, TAG, ring);
                    }
                    else if(repeat_count==7){
                        AlarmManagerUtil.setAlarm(mContext, 0, hour, minutes, 0, 0, TAG, ring);
                    }
                    else{
                        for(int i=0;i<7;i++){
                            if(date_is_select.get(date_id[i])){
                                if(i==0)
                                    AlarmManagerUtil.setAlarm(this, 2,hour, minutes, i, 7, TAG, ring);
                                else
                                    AlarmManagerUtil.setAlarm(this, 2,hour, minutes, i, i, TAG, ring);
                            }
                        }
                    }
                }
                Toast.makeText(this, "闹钟设置成功", Toast.LENGTH_LONG).show();
                save();
            }
            else{
                finish();
            }
            return super.onOptionsItemSelected(item);
    }

    public void save(){
        SQLiteDatabase db=dbOpenHandler.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("hour",hour);
        cv.put("minute",minutes);
        cv.put("TAG",TAG);
        cv.put("is_work",is_work);
        cv.put("ring",ring);
        String work_day=null;
        for(int i=0;i<7;i++){
            if(date_is_select.get(date_id[i])){
                if(work_day==null)
                    work_day=String.valueOf(i);
                else
                    work_day=work_day+","+String.valueOf(i);
            }
        }
        cv.put("work_day",work_day);
        Cursor cursor=db.rawQuery("SELECT * from alarm",null);
        if(cursor.getCount()!=0){
            db.update("alarm",cv,null,null);
        }
        else{
            db.insert("alarm",null,cv);
        }
    }

    private void showTimePick() {
        if (timeDialog == null) {
            timeDialog=new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    TextView textView=(TextView) findViewById(R.id.timeselect);
                    textView.setText(String.format("%02d:%02d",hourOfDay,minute));
                    hour=hourOfDay;
                    minutes=minute;
                }
            }, 0, 0, true);
        }
        timeDialog.show();
    }


    public void selectRingWay() {
        SelectRemindWayPopup fp = new SelectRemindWayPopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindWayPopupListener(new SelectRemindWayPopup
                .SelectRemindWayPopupOnClickListener() {

            @Override
            public void obtainMessage(int flag) {
                switch (flag) {
                    // 震动
                    case 0:
                        tv_ring_value.setText("震动");
                        ring = 0;
                        break;
                    // 铃声
                    case 1:
                        tv_ring_value.setText("铃声");
                        ring = 1;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public  void  setTag(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setTitle("闹钟提示");
        dialogBuilder.setView(R.layout.tag_dialog);
        EditText tag=(EditText)alertDialog.findViewById(R.id.tag_editText);
        tag.setText(TAG);
        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which){
                EditText tag=(EditText)alertDialog.findViewById(R.id.tag_editText);
                TAG=tag.getText().toString();
                return;
            }
        });
        alertDialog=dialogBuilder.create();
        alertDialog.show();
    }

    public  void restart(){
        SQLiteDatabase db=dbOpenHandler.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from alarm",null);
        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            hour=cursor.getInt(cursor.getColumnIndex("hour"));
            minutes=cursor.getInt(cursor.getColumnIndex("minute"));
            is_work=cursor.getInt(cursor.getColumnIndex("is_work"))==1;
            ring=cursor.getInt(cursor.getColumnIndex("ring"));
            String work_day= cursor.getString(cursor.getColumnIndex("work_day"));
            TAG=cursor.getString(cursor.getColumnIndex("TAG"));
            String[] days=work_day.split(",");
            for(int i=0;i<days.length;i++){
                repeat_count++;
                int index=Integer.parseInt(days[i]);
                date_is_select.put(date_id[index],true);
            }
            setReaptHint();
        }
        TextView time=(TextView)findViewById(R.id.timeselect);
        time.setText(String .format("%02d:%02d",hour,minutes));
        for(int i=0;i<7;i++){
            if(date_is_select.get(date_id[i])){
                TextView tmp=(TextView)findViewById(date_id[i]);
                tmp.setBackgroundResource(R.drawable.gray_back);
            }
        }
        if(is_work){
            Switch tmp=(Switch)findViewById(R.id.alarm_switch);
            tmp.setChecked(is_work);
        }
        if(ring==1){
            TextView tmp=(TextView)findViewById(R.id.hint_way);
            tmp.setText("铃声");
        }
    }


    public  void setReaptHint(){
        String tmp=null;
        int count=0;
        for(int i=0;i<date_is_select.size();i++){
            if(date_is_select.get(date_id[i])){
                count++;
                if(tmp==null){
                    tmp=date[i];
                }
                else{
                    tmp=tmp+","+date[i];
                }
            }
        }
        if(count==7)
            tmp="每天";
        repeat_count=count;
        TextView repeat=(TextView)findViewById(R.id.date_selected);
        repeat.setText(tmp);
    }
}




