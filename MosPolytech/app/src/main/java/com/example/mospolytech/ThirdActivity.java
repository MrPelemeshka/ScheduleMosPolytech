package com.example.mospolytech;

import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;


public class ThirdActivity extends AppCompatActivity {

    DataAdapter mA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        mA = ((MyApplication) this.getApplication()).getmAdapter();

        final ListView g = (ListView) findViewById(R.id.list_view);
        g.setAdapter(mA);

    }

}

