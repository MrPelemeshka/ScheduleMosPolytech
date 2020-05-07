package com.example.mospolytech;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.database.Cursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Объявим переменные компонентов
    public final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    Spinner spinner;
    TextView textView;
    DataAdapter mA;

    //Переменная для работы с БД
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mDb = ((MyApplication) this.getApplication()).getmDb();
        mA = ((MyApplication) this.getApplication()).getmAdapter();

        //Найдем компоненты в XML разметке
        spinner = (Spinner) findViewById(R.id.spinner);

        try {
            Cursor c = mDb.rawQuery("SELECT * FROM Groups", null);
            List InventoryList = new ArrayList();
            Integer i = 0;
            if (c.moveToFirst()) {
                do {
                    InventoryList.add(i, c.getString(1));
                    i += 1;
                } while (c.moveToNext());
            }
            c.close();

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, InventoryList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
        } catch (Exception e) {
        }

    }

    public void TwoSchedule(View v) {
        // действия, совершаемые после нажатия на кнопку
        String group = spinner.getSelectedItem().toString();
        Cursor cursor1 = mDb.rawQuery("SELECT _id FROM Groups WHERE Name = ?", new String[]{group});
        cursor1.moveToFirst();
        final ListView g = (ListView) findViewById(R.id.lv_now);
        g.setAdapter(mA);
        ((MyApplication) this.getApplication()).getmAdapter().refill(cursor1.getInt(0),0,1);
    }


    //Пропишем обработчик клика кнопки для Расписания на всю неделю
    public void allSchedule(View v) {
        // действия, совершаемые после нажатия на кнопку
        String group = spinner.getSelectedItem().toString();
        Cursor cursor1 = mDb.rawQuery("SELECT _id FROM Groups WHERE Name = ?", new String[]{group});
        cursor1.moveToFirst();
        // Создаем объект Intent для вызова новой Activity
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(EXTRA_MESSAGE, cursor1.getInt(0));
        cursor1.close();
        startActivity(intent);
    }
}