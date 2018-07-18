package com.example.seowoo.driverevision;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Permission;

public class LifeBookMarkMain extends AppCompatActivity {

    private static final String INIT_DATE = "2001-01-01 00:00:00";
    private static final String TAG = "LifeBookMarkMain";
    public static LifeBookMarkMain lifeBookMarkMain = null;
    private boolean initSettingType = false;
    private Handler pageMoveHandle = null;
    private SharedPreferences pref = null;
    private ProgressDialog progress = null;
    private String today = "";
    private String result = "";
    private static boolean network;


    //메인쓰레드는 ui를 못건드리고 별도의 쓰레드를 이용한다?
    class InitAsyncTask extends AsyncTask<Void, Void, String>
    {
        String targert;
        ProgressDialog dialog = new ProgressDialog(LifeBookMarkMain.this);
        String phone = getPhoneNumber();


        //실행하기 전에 사용되는 메소드
        @Override
        protected void onPreExecute() {

            network = false;

            //"http://alsmwsk3.dothome.co.kr/Android/ScheduleList.php?uuid="
            try{
                targert = "http://192.168.219.101:8181/test/androidDB.jsp?phone=" + URLEncoder.encode(phone,"UTF-8");
                dialog.setMessage("로딩중");
                dialog.setCancelable(false);
                dialog.show();
                //wait(3000);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //좀 많이 어려움
        @Override
        protected String doInBackground(Void... voids) {
            try{


                URL url = new URL(targert);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();
                //넘어오는 결과값을 저장할수 있도록 하는 메소드..

                //jsp와 통신 성공 시 수행
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    String temp;
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();

                    // jsp에서 보낸 값을 받는 부분
                    while ((temp = reader.readLine()) != null) {
                        stringBuilder.append(temp + "\n");
                    }

                    reader.close();
                    inputStreamReader.close();
                    conn.disconnect();


                    return stringBuilder.toString().trim();
                } else {
                    // 통신 실패
                    dialog.dismiss();
                    Toast.makeText(LifeBookMarkMain.this, "통신 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        //데이터가 만개 있는데 중간에 한번 실행되는 메소드
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        //해당 결과를 처리 할 수 있는 메소드..
        //데이터를 전부 다 받아온 후에 실행되는 메소드
        @Override
        protected void onPostExecute(String result) {


            try {
                Log.d(TAG, "onPostExecute: result: " + result);
                JSONObject jsonObject = new JSONObject(result);
                String string = jsonObject.getString("result");

                //result = jsonObject.getString("result");
                //Log.d(TAG, "onPostExecute: result" + string);


                dialog.dismiss();
                network = true;
                Toast.makeText(LifeBookMarkMain.this, string, Toast.LENGTH_SHORT)
                        .show();

            }catch (Exception e){
                e.printStackTrace();;
            }


        }
    }

    // 단말기 핸드폰번호 얻어오기
    public String getPhoneNumber() {
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

//        return mgr.getLine1Number();
        return "010-1234-5678";

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);
        new InitAsyncTask().execute();


    }

    public void gosetting(boolean network){
        if (network){
            Intent intent = new Intent(this, SettingActivity);
        }
    }
}
