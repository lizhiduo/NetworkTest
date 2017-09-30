package com.example.lizd.networktest;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView responseText;
    Button response;

    private final String TAG = "network_demo";
    private static final int READ_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseText = (TextView) findViewById(R.id.response_text);

        response = (Button) findViewById(R.id.send_request);
        response.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_request:
//                sendRequest();
                sendRequestWithOkHttp();
                break;
        }
    }


    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://www.baidu.com").build();

                    Response response = client.newCall(request).execute();

                    String responseData = response.body().toString();
                    showResponse(responseData);

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void sendRequest(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try{
                        URL url = new URL("https://www.baidu.com");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in = connection.getInputStream();

                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while(( line = reader.readLine()) != null){
                            response.append(line);
                        }

                        showResponse(response.toString());

                    }catch(Exception e){
                        e.printStackTrace();
                    }finally {
                        if(reader != null){
                            try{
                                reader.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        if(connection != null){
                            connection.disconnect();
                        }
                    }
                }
        }).start();
    }

    private void showResponse(final String response){
        //
        Message message = Message.obtain();
        message.obj = response;
        message.what = READ_OK;
        handler.sendMessage(message);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            if(msg.what == READ_OK){
                String data = ""+msg.obj;
                Log.d(TAG, "data: "+ data);
                responseText.setText(data);
            }
        }
    };
}
