package com.example.mospolytech;

import android.app.Application;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

public class MyApplication extends Application {

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

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

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