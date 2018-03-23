package mg.studio.weatherappdesign;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends AppCompatActivity {
    boolean net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        net = getNetState(this);
    }

    public static boolean getNetState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService((Context.CONNECTIVITY_SERVICE));
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }
        return false;
    }

    public void btn_location_click(View v) {
        Intent intent_location = new Intent(MainActivity.this, Activity_weather_update.class);
        startActivityForResult(intent_location, 1);
    }

    protected void onActivityResult(int requsetCode, int resultCode, Intent data) {
        switch (requsetCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String location = data.getStringExtra("data_return");
                    Button btn_location = (Button) findViewById(R.id.btn_location);
                    btn_location.setText(location);
                }
                break;
            default:
        }

    }

    public String getweek_day() {
        String[] week_day = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        Calendar c = Calendar.getInstance();
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        return week_day[w];
    }

    private void sendRequestWithHttpClient(final String loc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    if (net == true) {
                        URL url = new URL("http://wthrcdn.etouch.cn/WeatherApi?city=" + loc);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        parseXmldata(response.toString());
                    } else {
                        Toast.makeText(MainActivity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseXmldata(String data) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(data));
            int eventType = xmlPullParser.getEventType();
            String temp = " ";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:{
                        if ("wendu".equals(xmlPullParser.getName())) {
                            temp = xmlPullParser.nextText();
                            }
                        break;
                        }
                    case XmlPullParser.END_TAG:{
                        if ("resp".equals(xmlPullParser.getName())) {
                            TextView text_temperature = (TextView) findViewById(R.id.text_temperature);
                            text_temperature.setText(temp);
                            }
                        break;
                        }
                        default:
                            break;
                    }
                        eventType = xmlPullParser.next();
                }
            }catch(Exception e)
                {
                    e.printStackTrace();
                }
        }



    public void refre(){
        Calendar c = Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH) + 1;
        int day=c.get(Calendar.DAY_OF_MONTH);
        TextView text_date = (TextView) findViewById(R.id.text_date);
        TextView text_temperature = (TextView) findViewById(R.id.text_temperature);
        TextView text_day = (TextView) findViewById(R.id.text_day);
        LinearLayout linearLayout=findViewById(R.id.linearLayout1);
        TextView text_mon = (TextView) findViewById(R.id.text_mon);
        TextView text_tue = (TextView) findViewById(R.id.text_tue);
        Button button=(Button) findViewById(R.id.button);
        Button btn_location=(Button) findViewById(R.id.btn_location);
        text_date.setText(day+"/"+month+"/"+year);
        //text_temperature.setText("20");
        text_day.setText(getweek_day());
        linearLayout.setBackgroundColor(Color.rgb(237,145,33));
        text_tue.setBackgroundResource(R.drawable.yellow_circle);
        text_mon.getBackground().setAlpha(0);
        button.setBackgroundResource(R.drawable.button_shape_refresh);
        String loc = btn_location.getText().toString();
        sendRequestWithHttpClient(loc);
    }

    public void btn_refresh_click(View v){
        refre();
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://mpianatra.com/Courses/info.txt";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
            ((TextView) findViewById(R.id.text_temperature)).setText(temperature);
        }

    }
}
