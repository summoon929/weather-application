package mg.studio.weatherappdesign;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class Activity_weather_update extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_update);
        Button location_ok = (Button) findViewById(R.id.btn_location_ok);
        location_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_pro = findViewById(R.id.et_province);
                String province = et_pro.getText().toString();
                EditText et_city = findViewById(R.id.et_city);
                String city = et_city.getText().toString();
                String loc = province + "/" + city;
                Intent intent = new Intent();
                intent.putExtra("data_return", city);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
