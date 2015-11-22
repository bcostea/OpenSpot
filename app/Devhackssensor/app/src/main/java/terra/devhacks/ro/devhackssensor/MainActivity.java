package terra.devhacks.ro.devhackssensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String spotId = "565105867c274cbf71ee5dd7";
    private static final String host = "192.168.3.5";
    private static final String SPOT_URL = "http://" + host + ":8080/api/public/spots/" + spotId + "/update";
    private static Map<String, String> trueMap;
    private static Map<String, String> falseMap;
    static {
        trueMap = new HashMap<>();
        falseMap = new HashMap<>();
        trueMap.put("id", spotId);
        trueMap.put("free", "true");
        falseMap.put("id", spotId);
        falseMap.put("free", "false");

    }

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView sensorStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorStatus = (TextView) findViewById(R.id.sensorStatus);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        RequestQueue queue = Volley.newRequestQueue(this);
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == 0) {
                sensorStatus.setText("occupied");
                new DownloadWebpageTask().execute(SPOT_URL, "occupied");
            } else {
                sensorStatus.setText("free");
                new DownloadWebpageTask().execute(SPOT_URL, "free");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return sendRquest(params[0], params[1]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private String sendRquest(String urlp, String status) throws IOException {
            URL url = new URL(urlp);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf8");
            connection.setReadTimeout(25000);
            connection.setConnectTimeout(25000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            JSONObject postParams;
            if (status.equals("occupied")) {
                postParams = new JSONObject(falseMap);
            } else if (status.equals("free")) {
                postParams = new JSONObject(trueMap);
            } else {
                postParams = new JSONObject();
            }
            OutputStream out = connection.getOutputStream();
            out.write(postParams.toString().getBytes("UTF-8"));
            out.close();

            int response = connection.getResponseCode();

            InputStream is = connection.getInputStream();
            String contentAsString = IOUtils.toString(is, "UTF-8");
            Log.d(MainActivity.class.toString(), contentAsString);
            return contentAsString;
        }
    }
}
