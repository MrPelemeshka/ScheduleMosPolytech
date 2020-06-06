package com.example.mospolytech;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.database.Cursor;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Объявим переменные компонентов
    ArrayList<HashMap<String, String>> contactList;
    public final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private String TAG = MainActivity.class.getSimpleName();
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
        contactList = new ArrayList<>();
        new GetSchedule().execute();

        try {
            Cursor c = mDb.rawQuery("SELECT * FROM Groups", null);
            List InventoryList = new ArrayList();
            int i = 0;
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
        if (spinner.getSelectedItem() != null) {
            String group = spinner.getSelectedItem().toString();
            Cursor cursor1 = mDb.rawQuery("SELECT _id FROM Groups WHERE Name = ?", new String[]{group});
            cursor1.moveToFirst();
            final ListView g = (ListView) findViewById(R.id.lv_now);
            g.setAdapter(mA);
            ((MyApplication) this.getApplication()).getmAdapter().refill(cursor1.getInt(0), 0, 1);
            cursor1.close();
        }

    }

    public void onDaySchedule(View v) {
        // действия, совершаемые после нажатия на кнопку
        String group = spinner.getSelectedItem().toString();
        Cursor cursor1 = mDb.rawQuery("SELECT _id FROM Groups WHERE Name = ?", new String[]{group});
        cursor1.moveToFirst();
        final ListView g = (ListView) findViewById(R.id.lv_now);
        g.setAdapter(mA);
        ((MyApplication) this.getApplication()).getmAdapter().refill(cursor1.getInt(0), 0, 1);
        cursor1.close();
    }

    @Override
    public void onResume() {
        // действия, совершаемые после нажатия на кнопку
        super.onResume();
        if (spinner.getSelectedItem() != null) {
            String group = spinner.getSelectedItem().toString();
            Cursor cursor1 = mDb.rawQuery("SELECT _id FROM Groups WHERE Name = ?", new String[]{group});
            cursor1.moveToFirst();
            final ListView g = (ListView) findViewById(R.id.lv_now);
            g.setAdapter(mA);
            ((MyApplication) this.getApplication()).getmAdapter().refill(cursor1.getInt(0), 0, 1);
            cursor1.close();
        }
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

    @SuppressLint("StaticFieldLeak")
    private class GetSchedule extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        public Void doInBackground(Void... voids) {
            final char dm =(char) 34;
            HttpHandler hhandler = new HttpHandler();
            mDb.execSQL("DELETE from Groups");
            // Making a request to url and getting response
            String jsonStr;
            String url = "https://rasp.dmami.ru/groups-list.json";
            jsonStr = hhandler.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray arrGroups = jsonObj.getJSONArray("groups");
                    String strName;
                    for (int i = 0; i < arrGroups.length(); i++) {
                        strName = arrGroups.getString(i);
                        mDb.execSQL("Insert into Groups(_id,Name) values(" + i + ","+dm + strName + dm+ ")");
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

            jsonStr = hhandler.makeServiceCall("https://rasp.dmami.ru/site/group?group=191-362&session=0");
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONObject group = jsonObj.getJSONObject("group");
                    String evening = group.getString("evening");

                    // Getting JSON Object grid
                    JSONObject grid = jsonObj.getJSONObject("grid");

                    // looping through All days in week
                    for (int i = 1; i < 7; i++) {
                        JSONObject dof = grid.getJSONObject(String.valueOf(i));
                        String day_of_week = String.valueOf(i);
                        for (int j = 1; j < 7; j++) {
                            String class_time = String.valueOf(j);
                            JSONArray ct = dof.getJSONArray(String.valueOf(j));
                            for (int k = 0; k < ct.length(); k++) {
                                JSONObject row = ct.getJSONObject(k);
                                String sbj = row.getString("sbj");
                                String teacher = row.getString("teacher");
                                String dts = row.getString("dts");
                                String df = row.getString("df");
                                String dt = row.getString("dt");
                                String title = null;
                                JSONArray auditories = row.getJSONArray("auditories");
                                for (int l = 0; l < auditories.length(); l++) {
                                    JSONObject audi = auditories.getJSONObject(l);
                                    title = audi.getString("title");
                                }
                                String type = row.getString("type");
                                String week = row.getString("week");
                                String fm = row.getString("fm");
                                String sm = row.getString("sm");
                                String no = row.getString("no");
                                String wl = row.getString("wl");

                                // tmp hash map for single contact
                                HashMap<String, String> schedule = new HashMap<>();

                                // adding each child node to HashMap key => value
                                schedule.put("evening", evening);
                                schedule.put("day of week", day_of_week);
                                schedule.put("class time", class_time);
                                schedule.put("sbj", sbj);
                                schedule.put("teacher", teacher);
                                schedule.put("dts", dts);
                                schedule.put("df", df);
                                schedule.put("dt", dt);
                                schedule.put("title", title);
                                schedule.put("type", type);
                                schedule.put("week", week);
                                schedule.put("fm", fm);
                                schedule.put("sm", sm);
                                schedule.put("no", no);
                                schedule.put("wl", wl);


                                // adding schedule to contact list

                            }
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return  null;
        }
    }
}