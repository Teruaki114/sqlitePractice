package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editTextKey, editTextValue;
    private TestOpenHelper helper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextKey = findViewById(R.id.edit_text_key);
        editTextValue = findViewById(R.id.edit_text_value);

        textView = findViewById(R.id.text_view);

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetData();
            }
        });

        Button insertButton = findViewById(R.id.button_insert);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helper == null){
                    helper = new TestOpenHelper(getApplicationContext());
                }

                if(db == null){
                    db = helper.getWritableDatabase();
                }

                String key = editTextKey.getText().toString();
                String value = editTextValue.getText().toString();

                // 価格は整数を想定
                insertData(db, key, Integer.parseInt(value));
            }
        });

        Button readButton = findViewById(R.id.button_read);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });

        readData();
    }


    private void resetData(){
        if(textView != null) {
            db.delete("testdb", null, null);
            Toast.makeText(this, "全データを削除しました。", Toast.LENGTH_SHORT).show();
            readData();
        }
    }


    private void readData(){
        if(helper == null){
            helper = new TestOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }
        Log.d("debug","**********Cursor");

        Cursor cursor = db.query(
                "testdb",
                new String[] { "company", "stockprice" },
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        StringBuilder sbuilder = new StringBuilder();

        for (int i = 0; i < cursor.getCount(); i++) {
            sbuilder.append(cursor.getString(0));
            sbuilder.append(": ");
            sbuilder.append(cursor.getInt(1));
            sbuilder.append("\n");
            cursor.moveToNext();
        }

        // 忘れずに！
        cursor.close();

        Log.d("debug","**********"+sbuilder.toString());
        textView.setText(sbuilder.toString());
    }

    private void insertData(SQLiteDatabase db, String com, int price){
            ContentValues values = new ContentValues();
            values.put("company", com);
            values.put("stockprice", price);

            db.insert("testdb", null, values);
            editTextKey.setText("");
            editTextValue.setText("");
            Toast.makeText(this, "データを保存しました。", Toast.LENGTH_SHORT).show();

            readData();
    }
}