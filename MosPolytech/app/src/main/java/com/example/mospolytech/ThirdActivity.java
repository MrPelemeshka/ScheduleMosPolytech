package com.example.mospolytech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

public class ThirdActivity extends AppCompatActivity {

    public static String schedule;
    private TextView mSelectText;
    int GroupID;
    int DayID;
    TextView tv;
    DataAdapter mA;


    //Переменная для работы с БД
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);


        mDb = ((MyApplication) this.getApplication()).getmDb();
        mA = ((MyApplication) this.getApplication()).getmAdapter();


        // Получаем сообщение из объекта intent
        Intent intent = getIntent();


/*        tv = (TextView)findViewById(R.id.tv_a_day);
        tv.setText(schedule);*/

/*        final GridView g = (GridView) findViewById(R.id.gridView1);
        g.setAdapter(mA);*/

        final ListView g = (ListView) findViewById(R.id.list_view);
        g.setAdapter(mA);

    }

}

