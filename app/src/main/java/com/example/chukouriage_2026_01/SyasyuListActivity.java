package com.example.chukouriage_2026_01;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SyasyuListActivity extends AppCompatActivity {
    private SQLiteMyHelper helper;
    private SQLiteDatabase db;
    private ArrayList<String> syasyuList;   // 表示用
    private ArrayList<Integer> idList;      // ID 保持用（編集用）
    private ListView listView;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syasyu_list);

        helper = new SQLiteMyHelper(this);
        listView = findViewById(R.id.lvSyasyu);
        btnAdd = findViewById(R.id.btnAddSyasyu);

        loadSyasyuList();  // 一覧読み込み

        // 追加ボタン
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, SyasyuAddActivity.class);
            startActivity(intent);
        });

        // 編集画面へ遷移
        listView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedId = idList.get(position);

            Intent intent = new Intent(this, SyasyuEditActivity.class);
            intent.putExtra("id", selectedId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSyasyuList();  // 画面復帰時に最新化
    }

    // 車種一覧を読み込む
    private void loadSyasyuList() {
        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, syasyu, tanka FROM tblSyasyu ORDER BY _id", null);

        syasyuList = new ArrayList<>();
        idList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String syasyu = cursor.getString(1);
                int tanka = cursor.getInt(2);

                idList.add(id);
                syasyuList.add(id + " : " + syasyu + "（" + tanka + "円）");

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, syasyuList);

        listView.setAdapter(adapter);
    }
}
