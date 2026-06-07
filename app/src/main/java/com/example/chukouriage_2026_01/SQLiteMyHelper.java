package com.example.chukouriage_2026_01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteMyHelper extends SQLiteOpenHelper {
    private static SQLiteMyHelper instance = null;//自分自身への参照を用意する
    private static final String DB_NAME = "shiten_uriage.db";
    private static final int DB_VERSION = 1;
    public static final String DB_TABLE = "tbluriage";

    public static final String DB_SYASYU = "tblSyasyu";

    public SQLiteMyHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public SQLiteMyHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 最初にテーブルをcreate(存在しないとき)
        sqLiteDatabase.execSQL("create table if not exists " + DB_TABLE
                + "(_id integer primary key autoincrement, "
                + "shiten text,syasyu text,tanka integer,daisu integer)");

        //車種、価格　テーブルを作成する
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DB_SYASYU
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "syasyu TEXT NOT NULL,tanka INTEGER NOT NULL)");




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_TABLE);
        onCreate(db);

    }
    //インスタンスが空ならば、インスタンスを返す
    public static SQLiteMyHelper getInstance(Context context){
        if (instance == null){
            instance = new SQLiteMyHelper(context,DB_NAME,null,DB_VERSION);
        }
        return instance;
    }

}
