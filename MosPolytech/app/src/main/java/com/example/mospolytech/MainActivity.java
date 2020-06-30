package com.example.mospolytech;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.database.Cursor;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private String TAG = MainActivity.class.getSimpleName();
    AutoCompleteTextView spinner;
    DataAdapter mA;
    String selected = null;

    // Переменная для работы с БД
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Подключение БД
        mDb = ((MyApplication) this.getApplication()).getmDb();

        new GetGroups().execute();

        // Найдем компоненты в XML разметке
        spinner = (AutoCompleteTextView) findViewById(R.id.spinner);

        // Подключение адаптера
        mA = ((MyApplication) this.getApplication()).getmAdapter();

        // Заносим список групп в выпадающий список поиска группы
        try {
            Cursor c = mDb.rawQuery("SELECT * FROM Groups", new String[]{});
            System.out.println(c.getString(1));
            String[] InventoryList = new String[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    System.out.println(c.getString(1));
                    InventoryList[i] = c.getString(1);
                    i += 1;
                } while (c.moveToNext());
            }
            c.close();

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_dropdown_item_1line, InventoryList);
            dataAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);

        } catch (Exception e) {
            System.out.println(e);
        }
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = spinner.getAdapter().getItem(position).toString();
            }
        });
    }

    public void onDaySchedule(View v) {
        // действия, совершаемые после нажатия на кнопку
        String group = selected;
        if (selected != null) {
            new GetSchedule().execute();
            mDb = ((MyApplication) this.getApplication()).getmDb();
            Cursor cursor1 = mDb.rawQuery("SELECT _id" +
                            " FROM Groups WHERE Name = ?",
                    new String[]{group});
            cursor1.moveToFirst();
            final ListView g = (ListView) findViewById(R.id.lv_now);
            g.setAdapter(mA);
            ((MyApplication) this.getApplication()).getmAdapter().refill
                    (cursor1.getInt(0), 0, 1);
            cursor1.close();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Такой группы нет в нашей базе данных.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selected != null) {
            new GetSchedule().execute();
            mDb = ((MyApplication) this.getApplication()).getmDb();
            String group = selected;
            Cursor cursor1 = mDb.rawQuery("SELECT _id FROM " +
                            "Groups WHERE Name = ?",
                    new String[]{group});
            cursor1.moveToFirst();
            final ListView g = (ListView) findViewById(R.id.lv_now);
            g.setAdapter(mA);
            ((MyApplication) this.getApplication()).getmAdapter().refill
                    (cursor1.getInt(0), 0, 1);
            cursor1.close();
        }
    }


    //Пропишем обработчик клика кнопки для Расписания на всю неделю
    public void allSchedule(View v) {

        String group = selected;
        if (selected != null) {
            new GetSchedule().execute();
            mDb = ((MyApplication) this.getApplication()).getmDb();
            Cursor cursor1 = mDb.rawQuery("SELECT _id" +
                            " FROM Groups WHERE Name = ?",
                    new String[]{group});
            cursor1.moveToFirst();
            // Создаем объект Intent для вызова новой Activity
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra(EXTRA_MESSAGE, cursor1.getInt(0));
            cursor1.close();
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Такой группы нет в нашей базе данных.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetGroups extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,
                    "Data is downloading",
                    Toast.LENGTH_LONG).show();
        }


        @Override
        public Void doInBackground(Void... voids) {
            final char dm = (char) 34;
            HttpHandler hhandler = new HttpHandler();
            mDb.execSQL("DELETE from Groups");
            String jsonStr;
            String url = "https://rasp.dmami.ru/groups-list.json";
            jsonStr = hhandler.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray arrGroups = jsonObj.getJSONArray("groups");
                    String groupName;
                    for (int i = 0; i < arrGroups.length(); i++) {
                        groupName = arrGroups.getString(i);
                        int sum = i + 1;
                        mDb.execSQL("Insert into Groups(_id,Name)" +
                                " values(" + dm + sum + dm + "," + dm + groupName + dm + ")");
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Проверьте подключение к интернету " +
                                        "и перзапустите приложение",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GetSchedule extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,
                    "Json Data is downloading",
                    Toast.LENGTH_LONG).show();

        }


        @Override
        public Void doInBackground(Void... voids) {
            final char dm = (char) 34;
            HttpHandler hhandler = new HttpHandler();
            String jsonStr;
            Cursor cursor = null;
            JSONObject jsonObj = new JSONObject();
            String groupName;
            String groupID;
            cursor = mDb.rawQuery("select _id,Name from Groups where Name=?",
                    new String[]{selected});
            cursor.moveToFirst();
            groupName = cursor.getString(cursor.getColumnIndex("Name"));
            groupID = cursor.getString(cursor.getColumnIndex("_id"));
            mDb.execSQL("DELETE from Schedule where _id=" + groupID + "");
            cursor.close();
            jsonStr = hhandler.makeServiceCall
                    ("https://rasp.dmami.ru/site/group?group="
                            + groupName + "&session=1");
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    jsonObj = new JSONObject(jsonStr);
                    JSONObject group = jsonObj.getJSONObject("group");
                    String evening = group.getString("evening");
                    JSONObject grid = jsonObj.getJSONObject("grid");
                    for (int h = 1; h < 7; h++) {
                        JSONObject dof = grid.getJSONObject(String.valueOf(h));
                        String day_of_week = String.valueOf(h);
                        for (int j = 1; j < 7; j++) {
                            String class_time = String.valueOf(j);
                            JSONArray ct = dof.getJSONArray(String.valueOf(j));
                            for (int k = 0; k < ct.length(); k++) {
                                JSONObject row = ct.getJSONObject(k);
                                String sm = row.getString("sm");
                                if (sm.equals("true")) {
                                    String sbj = row.getString("sbj");
                                    String teacher = row.getString("teacher");
                                    String dts = row.getString("dts");
                                    String df = row.getString("df");
                                    String dt = row.getString("dt");
                                    String title = "";
                                    String roomName = "";
                                    String roomID;
                                    JSONArray auditories = row.getJSONArray("auditories");
                                    for (int l = 0; l < auditories.length(); l++) {
                                        JSONObject audi = auditories.getJSONObject(l);
                                        title = audi.getString("title");
                                        title = title.replaceAll("\\<.*?>", "");
                                        roomName = roomName + title + " ,";
                                    }
                                    if (!roomName.equals("")) {
                                        roomName = roomName.substring(0, roomName.length() - 1);
                                        cursor = mDb.rawQuery("select _id from" +
                                                " Rooms where Name=?", new String[]{roomName});
                                        if (cursor.getCount() < 1) {
                                            mDb.execSQL("insert into Rooms(Name)" +
                                                    " values(" + dm + roomName + dm + ")");
                                            cursor = mDb.rawQuery("select _id from Rooms" +
                                                    " where Name=?", new String[]{roomName});
                                        }
                                        assert cursor != null;
                                        cursor.moveToFirst();
                                        roomID = cursor.getString
                                                (cursor.getColumnIndex("_id"));
                                    } else {
                                        roomName = "Кабинет не указан";
                                        cursor = mDb.rawQuery("select _id from Rooms" +
                                                " where Name=?", new String[]{roomName});
                                        if (cursor.getCount() < 1) {
                                            mDb.execSQL("insert into Rooms(Name)" +
                                                    " values(" + dm + roomName + dm + ")");
                                            cursor = mDb.rawQuery("select _id from" +
                                                            " Rooms where Name=?",
                                                    new String[]{roomName});
                                        }
                                        assert cursor != null;
                                        cursor.moveToFirst();
                                        roomID = cursor.getString
                                                (cursor.getColumnIndex("_id"));
                                    }
                                    cursor.close();
                                    String type = row.getString("type");
                                    String week = row.getString("week");
                                    String fm = row.getString("fm");
                                    String no = row.getString("no");
                                    String wl = row.getString("wl");
                                    String teacherID;
                                    cursor = mDb.rawQuery("select _id from Teachers" +
                                            " where Name=?", new String[]{teacher});
                                    if (cursor.getCount() < 1) {
                                        mDb.execSQL("insert into Teachers(Name)" +
                                                " values(" + dm + teacher + dm + ")");
                                        cursor = mDb.rawQuery("select _id from Teachers" +
                                                " where Name=?", new String[]{teacher});
                                    }
                                    assert cursor != null;
                                    cursor.moveToFirst();
                                    teacherID = cursor.getString
                                            (cursor.getColumnIndex("_id"));
                                    cursor.close();
                                    String subjectID;
                                    cursor = mDb.rawQuery("select _id from Subjects where" +
                                            " Name=?", new String[]{sbj});
                                    if (cursor.getCount() < 1) {
                                        mDb.execSQL("insert into Subjects(Name)" +
                                                " values(" + dm + sbj + dm + ")");
                                        cursor = mDb.rawQuery("select _id from Subjects" +
                                                " where Name=?", new String[]{sbj});
                                    }
                                    assert cursor != null;
                                    cursor.moveToFirst();
                                    subjectID =
                                            cursor.getString
                                                    (cursor.getColumnIndex("_id"));
                                    cursor.close();

                                    cursor = mDb.rawQuery("select _id from Schedule where " +
                                                    "GroupID=? and DayID=? and SubjectID=?" +
                                                    " and TeacherID=?" +
                                                    " and RoomID=? and Regnr=? and Evening=?",
                                            new String[]{groupID, String.valueOf(h),
                                                    subjectID, teacherID, roomID,
                                                    String.valueOf(j), evening});
                                    if (cursor.getCount() < 1) {
                                        mDb.execSQL("insert into Schedule(GroupID,DayID,SubjectID," +
                                                "TeacherID,RoomID,Regnr,Evening)" +
                                                " values(" + dm + groupID + dm + "," +
                                                "" + dm + h + dm + "," + dm + subjectID + dm + "," +
                                                "" + dm + teacherID + dm + "," +
                                                "" + dm + roomID + dm + "," + dm + j + dm + "," +
                                                "" + dm + evening + dm + ")");
                                        cursor = mDb.rawQuery("select _id from Schedule " +
                                                "where GroupID=? and DayID=? and SubjectID=? and" +
                                                " TeacherID=? and RoomID=? and Regnr=? and" +
                                                " Evening=?", new String[]{groupID,
                                                String.valueOf(h), subjectID, teacherID,
                                                roomID, String.valueOf(j), evening});
                                    }
                                    assert cursor != null;
                                    cursor.moveToFirst();
                                    cursor.close();
                                }
                            }
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Проверьте подключение к интернету и перзапустите приложение",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            cursor.close();
            return null;
        }
    }
}