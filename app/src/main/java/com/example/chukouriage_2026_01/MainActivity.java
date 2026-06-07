package com.example.chukouriage_2026_01;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner spShiten,spSyasyu;
    Button button1,button2,button3;
    TextView textView;
    SQLiteMyHelper sqLiteMyHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //画面の部品への参照を取り出す
        spShiten = findViewById(R.id.spinner1);
        spSyasyu = findViewById(R.id.spinner2);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        textView = findViewById(R.id.textView);
        //垂直方向のスクロール機能を追加する
        textView.setMovementMethod(new ScrollingMovementMethod());
        // スクロールバーを有効化（念のため明示）
        textView.setVerticalScrollBarEnabled(true);

        // フォーカスを受け取れるようにする（超重要）
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.requestFocus();

        // 親にタッチイベントを奪われないようにする
        textView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // へスパークラスのインスタンスを取り出し、データベースの参照を取得
        sqLiteMyHelper=SQLiteMyHelper.getInstance(this);
        sqLiteDatabase=sqLiteMyHelper.getReadableDatabase();

        // ▼ 初期データが無ければ INSERT
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM tblSyasyu", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            sqLiteDatabase.execSQL("INSERT INTO tblSyasyu (syasyu, tanka) VALUES ('クラウン', 5000000)");
            sqLiteDatabase.execSQL("INSERT INTO tblSyasyu (syasyu, tanka) VALUES ('マークⅡ', 3000000)");
            sqLiteDatabase.execSQL("INSERT INTO tblSyasyu (syasyu, tanka) VALUES ('プリウス', 2500000)");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSyasyuToSpinner();
    }

    private void loadSyasyuToSpinner() {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT syasyu FROM tblSyasyu", null);

        ArrayList<String> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spSyasyu.setAdapter(adapter);
    }


    //onClick属性のイベント処理は　この場所に記述する
    //登録ボタン（btn1）クリック時のイベントハンドラー、
    public void onBtn1Click(View v){
        // 支店の取得
        String shitenMei = spShiten.getSelectedItem().toString();
        // 車種の取得
        String syasyuMei = spSyasyu.getSelectedItem().toString();
        // 金額の取得　データベースに車種と値段のテーブルを作り　そこで処理する
        int tankaKuruma = 0;

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT tanka FROM tblSyasyu WHERE syasyu = ?",
                new String[]{ syasyuMei }
        );

        if(cursor.moveToFirst()){
            tankaKuruma = cursor.getInt(0);
        }
        cursor.close();
        // 台数(1台)をセット
        int daisuKuruma = 1;
        // ContentValuesオブジェクトの作成
        ContentValues contentValues=new ContentValues();
        contentValues.put("shiten",shitenMei);//支店
        contentValues.put("syasyu",syasyuMei);//車種
        contentValues.put("tanka",tankaKuruma);//単価
        contentValues.put("daisu",daisuKuruma);//台数
        // データの登録
        sqLiteDatabase.insert(SQLiteMyHelper.DB_TABLE,null,contentValues);
        Toast.makeText(this,contentValues.toString(),Toast.LENGTH_LONG).show();
        // [売上台数]ボタン押下時の売上台数集計を実施
        //onBtn2Click(null);
    }

    public void onBtn2Click(View v){
        // 支店ごとの売上台数集計
        String sql = "select shiten,sum(daisu) from tbluriage group by shiten;";
        // 集計データの表示
        StringBuilder stringBuilder = new StringBuilder();
        try (Cursor cursor = sqLiteDatabase.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                stringBuilder.append(cursor.getString(0)).append(" : ")
                        .append(cursor.getString(1)).append(" 台\n");
            }
        }
        textView.setText(stringBuilder.toString());
        //垂直方向のスクロール機能を追加する
//        textView.setMovementMethod(new ScrollingMovementMethod());
        // ScrollViewを自動で一番下までスクロールさせる
//        final ScrollView scrollView = findViewById(R.id.scrollView);
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });


    }
    public void onBtn3Click(View view){
        // 支店ごとの総売上金額集計
        String sql = "select shiten,sum(tanka) from tbluriage group by shiten";
        // 集計データの表示
        StringBuilder stringBuilder = new StringBuilder();
        try (Cursor cursor = sqLiteDatabase.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                stringBuilder.append(cursor.getString(0)).append(" : ")
                        .append(String.format(java.util.Locale.getDefault(), "%,d", cursor.getInt(1)))
                        .append(" 円\n");
            }
        }
        textView.setText(stringBuilder.toString());
        // ScrollViewを自動で一番下までスクロールさせる
//        final ScrollView scrollView = findViewById(R.id.scrollView);
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });

    }
    public void onBtn4Click(View view){
        // DB内のレコード削除
        sqLiteDatabase.delete(SQLiteMyHelper.DB_TABLE,null,null);
        sqLiteDatabase.delete(SQLiteMyHelper.DB_TABLE,null,null);
        onBtn2Click(null);

    }
    public void btnClick_All(View v) {
        StringBuilder stringBuilder = new StringBuilder();
        Log.d("HHHH","btnClick_All_01");
        SQLiteDatabase db = sqLiteMyHelper.getReadableDatabase();
           //データのかたまりで取り出すクラス
        try (Cursor cs = db.query(SQLiteMyHelper.DB_TABLE, null, null, null, null, null, null)) {
            while (cs.moveToNext()) {
                stringBuilder.append(cs.getString(0)).append(":")
                        .append(cs.getString(1)).append(":")
                        .append(cs.getString(2)).append("\n");
            }
        }
        textView.setText(stringBuilder.toString());
        // ScrollViewを自動で一番下までスクロールさせる
//        final ScrollView scrollView = findViewById(R.id.scrollView);
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });


    }

    public void onShashuAllClick(View v) {
        // 車種ごとの売上台数集計
        Log.d("HHHH","onShashuAllClick_01");
        String sql = "select syasyu,sum(tanka) from tbluriage group by syasyu;";
        // 集計データの表示
        StringBuilder stringBuilder = new StringBuilder();
        try (Cursor cursor = sqLiteDatabase.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                stringBuilder.append(cursor.getString(0)).append(" : ")
                        .append(String.format(java.util.Locale.getDefault(), "%,d", cursor.getInt(1)))
                        .append(" 円\n");
            }
        }
        textView.setText(stringBuilder.toString());
        // ScrollViewを自動で一番下までスクロールさせる
//        final ScrollView scrollView = findViewById(R.id.scrollView);
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });

    }

    //各支店における、車種ごとの売上合計を表示する
    public void onShitenAllClick(View v) {
        Log.d("HHHH","onShitenAllClick_01");
        //String sql = "select shiten,sum(tanka) from tbluriage group by shiten";
        String sql = "SELECT shiten, syasyu, SUM(tanka) AS uriage_gokei " +
                "FROM tbluriage GROUP BY shiten, syasyu";
        Log.d("HHHH","onShitenAllClick_02");
        //車種ごとの売上合計を支店別に出すには、syasyu（車種）も GROUP BY に含める必要があり

        // 集計データの表示
        StringBuilder stringBuilder = new StringBuilder();
        Log.d("HHHH","onShitenAllClick_03");
        try (Cursor cursor_shiten = sqLiteDatabase.rawQuery(sql, null)) {
            while (cursor_shiten.moveToNext()) {
                String shiten = cursor_shiten.getString(0);          // 支店
                String syasyu = cursor_shiten.getString(1);          // 車種
                int uriage = cursor_shiten.getInt(2);                // 売上合計
                stringBuilder.append(shiten).append(" / ").append(syasyu).append(" : ")
                        .append(String.format(java.util.Locale.getDefault(), "%,d", uriage))
                        .append(" 円\n");
            }
        }
        Log.d("HHHH","onShitenAllClick_04");
        textView.setText(stringBuilder.toString());
        // ScrollViewを自動で一番下までスクロールさせる
//        final ScrollView scrollView = findViewById(R.id.scrollView);
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });

    }

    public void onSyasyuMasterClick(View v) {
        Log.d("tag:HHHH","onSyasyuMasterClick_01");//呼び出し確認
        Intent intent = new Intent(this, SyasyuListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();//DBのクローズ処理をここで実施
    }
}