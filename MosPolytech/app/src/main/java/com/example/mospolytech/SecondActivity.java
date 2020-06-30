package com.example.mospolytech;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class SecondActivity extends AppCompatActivity {

    TextView textView;
    Button mon;
    Button tue;
    Button wed;
    Button thu;
    Button fri;
    Button sat;
    int groupID;
    String schedule = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Получаем сообщение из объекта intent
        Intent intent = getIntent();
        groupID = intent.getIntExtra(MainActivity.EXTRA_MESSAGE, 2);

        textView = (TextView) findViewById(R.id.test);
        mon = (Button) findViewById(R.id.bt_mon);
        tue = (Button) findViewById(R.id.bt_tue);
        wed = (Button) findViewById(R.id.bt_wed);
        thu = (Button) findViewById(R.id.bt_thu);
        fri = (Button) findViewById(R.id.bt_fri);
        sat = (Button) findViewById(R.id.bt_sat);

    }


    public void Monday(View v) {
        Intent intent = new Intent(this, ThirdActivity.class);
        ((MyApplication) this.getApplication()).getmAdapter().refill(groupID, 1, 2);
        startActivity(intent);
    }

    public void Tuesday(View view) {
        Intent intent = new Intent(this, ThirdActivity.class);
        ((MyApplication) this.getApplication()).getmAdapter().refill(groupID, 2, 2);
        startActivity(intent);
    }

    public void Wednesday(View view) {
        Intent intent = new Intent(this, ThirdActivity.class);
        ((MyApplication) this.getApplication()).getmAdapter().refill(groupID, 3, 2);
        startActivity(intent);
    }

    public void Thursday(View view) {
        Intent intent = new Intent(this, ThirdActivity.class);
        ((MyApplication) this.getApplication()).getmAdapter().refill(groupID, 4, 2);
        startActivity(intent);
    }

    public void Friday(View view) {
        Intent intent = new Intent(this, ThirdActivity.class);
        ((MyApplication) this.getApplication()).getmAdapter().refill(groupID, 5, 2);
        startActivity(intent);
    }

    public void Saturday(View view) {
        Intent intent = new Intent(this, ThirdActivity.class);
        ((MyApplication) this.getApplication()).getmAdapter().refill(groupID, 6, 2);
        startActivity(intent);
    }
}
