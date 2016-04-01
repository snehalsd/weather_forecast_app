package com.example.snehalsurendradesai.weatherforecast;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private RadioGroup radioGroup;
    Spinner spinner;
    String error;
    EditText et1;
    TextView err;
    EditText et2;

    public void onLogoClicked(View view)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.forecast.io")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        spinner = (Spinner) findViewById(R.id.state);
        ArrayAdapter arradap = ArrayAdapter.createFromResource(this, R.array.statecodes, android.R.layout.simple_spinner_item);
        spinner.setAdapter(arradap);
        spinner.setOnItemSelectedListener(this);
    }

    public void onAboutClicked(View view){
        Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }

    public void clear (View view1){
        radioGroup = (RadioGroup) findViewById(R.id.degree);
        radioGroup.check(R.id.fahrenheit);
        EditText et1 = (EditText) findViewById(R.id.street);
        EditText et2 = (EditText) findViewById(R.id.city);
        err = (TextView) findViewById(R.id.errorMsg);
        et1.setText("");
        et2.setText("");
        err.setText("");
        spinner.setSelection(0);
    }
    public void Search(View view){
        int flag = 1;
        et1 = (EditText) findViewById(R.id.street);
        et2 = (EditText) findViewById(R.id.city);
        String street = et1.getText().toString();
        String city = et2.getText().toString();
        err = (TextView) findViewById(R.id.errorMsg);
        err.setText("");
        error="";

        if(street.matches("")){
            error="Please Enter the Street Address.\n";
            flag = 0;
        }
        if(city.matches("")){
            error=error+"Please Enter the City.\n";
            flag = 0;
        }
        spinner=(Spinner) findViewById(R.id.state);
        String state = spinner.getSelectedItem().toString();
        if(state.matches("Select")){
            error=error+"Please Enter the state.";
            flag = 0;
        }

        if(flag == 1)
        {
            String str1 = et1.getText().toString();
            String str2 = et2.getText().toString();
            String str3 = spinner.getSelectedItem().toString();
            radioGroup = (RadioGroup) findViewById(R.id.degree);
            int id = radioGroup.getCheckedRadioButtonId();
            RadioButton selected = (RadioButton)findViewById(id);
            String str4 = selected.getText().toString();
            connectWithHttpGet(str1, str2, str3, str4);
        }

        else
        {

            err.setText(error);
        }
    }

    private void connectWithHttpGet(String str1, String str2, String str3, String str4) {
        class HttpGetAsyncTask extends AsyncTask<String, Void, String> {

            String street1, city1, state1, degree1;
            @Override
            protected String doInBackground(String... params) {
                street1 = params[0];
                city1 = params[1];
                state1 = params[2];
                degree1 = params[3];


                String newstreet1 = street1.replaceAll(" ", "+");
                String newcity1 = city1.replaceAll(" ", "+");


                HttpClient httpClient = new DefaultHttpClient();


                HttpGet httpGet = new HttpGet("http://http://snehalmywebsite-env.elasticbeanstalk.com/index/index.php?address=" + newstreet1 + "&city=" + newcity1 + "&state=" + state1 + "&units=" + degree1.toLowerCase());

                try {

                    HttpResponse httpResponse = httpClient.execute(httpGet);

                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }

                    return stringBuilder.toString();
                } catch (ClientProtocolException cpe) {
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                if(result.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter A Valid Address", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("result", result);
                    extras.putString("city", city1);
                    extras.putString("state", state1);
                    extras.putString("degree", degree1);
                    resultIntent.putExtras(extras);
                    startActivity(resultIntent);
                }
            }
        }


        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();

        httpGetAsyncTask.execute(str1, str2, str3, str4);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
