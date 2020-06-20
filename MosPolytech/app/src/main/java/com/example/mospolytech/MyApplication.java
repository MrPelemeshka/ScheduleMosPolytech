package com.example.mospolytech;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyApplication extends Application {

    private static final String TAG = MainActivity.class.getSimpleName();;
    SQLiteDatabase mDb;
    DBHelper mDBHelper;
    public DataAdapter mAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        mDBHelper = new DBHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        mDb = mDBHelper.getWritableDatabase();

        mAdapter = new DataAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,mDb);
    }

    public SQLiteDatabase getmDb() {
        return mDb;
    }

    public DataAdapter getmAdapter() {
        return mAdapter;
    }


}