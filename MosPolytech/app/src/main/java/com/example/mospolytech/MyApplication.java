package com.example.mospolytech;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import java.io.IOException;

public class MyApplication extends Application {

    private static final String TAG = MainActivity.class.getSimpleName();
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
                android.R.layout.simple_list_item_1, mDb);
    }

    public SQLiteDatabase getmDb() {
        mDBHelper = new DBHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        mDb = mDBHelper.getWritableDatabase();

        return mDb;
    }

    public DataAdapter getmAdapter() {
        return mAdapter;
    }


}