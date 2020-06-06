package com.example.mospolytech;

import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;


public class ThirdActivity extends AppCompatActivity {

    DataAdapter mA;


    //Переменная для работы с БД
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);


        mDb = ((MyApplication) this.getApplication()).getmDb();
        mA = ((MyApplication) this.getApplication()).getmAdapter();

        final ListView g = (ListView) findViewById(R.id.list_view);
        g.setAdapter(mA);

    }

}

