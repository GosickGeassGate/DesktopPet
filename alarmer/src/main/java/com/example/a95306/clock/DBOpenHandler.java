package com.example.a95306.clock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 95306 on 2019-06-16.
 */

public class DBOpenHandler extends SQLiteOpenHelper {
    int version;
    public DBOpenHandler(Context context, String name,
                         SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.version=version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// 当数据库创建时就用SQL命令创建一个表
        db.execSQL("CREATE TABLE alarm(_id integer primary key autoincrement, hour int default 0, minute int default 0, is_work boolean default false, work_day text,TAG text,ring int default 0)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if(oldVersion<newVersion){
            db.execSQL("DROP TABLE IF EXISTS dict" );
            onCreate(db);
        }
    }
}
