package com.example.chukouriage_2026_01;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SyasyuEditActivity extends AppCompatActivity {
    private EditText etSyasyuEdit, etTankaEdit;
    private Button btnUpdate, btnDelete, btnCancel;
    private SQLiteMyHelper helper;
    private int syasyuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syasyu_edit);

        helper = new SQLiteMyHelper(this);

        etSyasyuEdit = findViewById(R.id.etSyasyuEdit);
        etTankaEdit = findViewById(R.id.etTankaEdit);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancelEdit);

        // SyasyuListActivity から渡された ID を取得
        syasyuId = getIntent().getIntExtra("id", -1);

        loadData();  // DB からデータ読み込み

        btnUpdate.setOnClickListener(v -> updateData());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnCancel.setOnClickListener(v -> finish());
    }

    // DB からデータ読み込み
    private void loadData() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT syasyu, tanka FROM tblSyasyu WHERE _id=?",
                new String[]{String.valueOf(syasyuId)}
        );

        if (cursor.moveToFirst()) {
            etSyasyuEdit.setText(cursor.getString(0));
            etTankaEdit.setText(String.valueOf(cursor.getInt(1)));
        }

        cursor.close();
        db.close();
    }

    // 更新処理
    private void updateData() {
        String syasyu = etSyasyuEdit.getText().toString().trim();
        String tankaStr = etTankaEdit.getText().toString().trim();

        if (syasyu.isEmpty() || tankaStr.isEmpty()) {
            Toast.makeText(this, "車種名と単価を入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        int tanka = Integer.parseInt(tankaStr);

        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE tblSyasyu SET syasyu=?, tanka=? WHERE _id=?",
                new Object[]{syasyu, tanka, syasyuId});
        db.close();

        Toast.makeText(this, "更新しました", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 削除確認ダイアログ
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("削除確認")
                .setMessage("本当に削除しますか？")
                .setPositiveButton("削除", (dialog, which) -> deleteData())
                .setNegativeButton("キャンセル", null)
                .show();
    }

    // 削除処理
    private void deleteData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM tblSyasyu WHERE _id=?",
                new Object[]{syasyuId});
        db.close();

        Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show();
        finish();
    }
}
