package com.example.chukouriage_2026_01;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SyasyuAddActivity extends AppCompatActivity {


    private EditText etSyasyu, etTanka;
    private Button btnCancel;
    private SQLiteMyHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syasyu_add);

        helper = new SQLiteMyHelper(this);

        etSyasyu = findViewById(R.id.etSyasyu);
        etTanka = findViewById(R.id.etTanka);
        Button btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // 保存ボタン
        btnSave.setOnClickListener(v -> saveSyasyu());

        // キャンセル
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveSyasyu() {
        String syasyu = etSyasyu.getText().toString().trim();
        String tankaStr = etTanka.getText().toString().trim();

        if (syasyu.isEmpty() || tankaStr.isEmpty()) {
            Toast.makeText(this, "車種名と単価を入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        int tanka = Integer.parseInt(tankaStr);

        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("INSERT INTO tblSyasyu(syasyu, tanka) VALUES(?, ?)",
                new Object[]{syasyu, tanka});
        db.close();

        Toast.makeText(this, "登録しました", Toast.LENGTH_SHORT).show();

        finish();  // 一覧画面に戻る
    }
}
