package com.example.seowoo.driverevision;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Permission;

public class LifeBookMarkMain extends AppCompatActivity {

    private static final String INIT_DATE = "2001-01-01 00:00:00";
    private static final String TAG = "LifeBookMarkMain";
    public static LifeBookMarkMain lifeBookMarkMain = null;
    private InitAsyncTask initAsyncTask = null;
    private boolean initSettingType = false;
    private Handler pageMoveHandle = null;
    private SharedPreferences pref = null;
    private ProgressDialog progress = null;
    private String today = "";


    //메인쓰레드는 ui를 못건드리고 별도의 쓰레드를 이용한다?
    class InitAsyncTask extends AsyncTask<Void, Void, String>
    {
        String targert;
        ProgressDialog dialog = new ProgressDialog(LifeBookMarkMain.this);
        String phone = getPhoneNumber();


        //실행하기 전에 사용되는 메소드
        @Override
        protected void onPreExecute() {

            try{
                targert = "http://alsmwsk3.dothome.co.kr/Android/ScheduleList.php?uuid=" + URLEncoder.encode(phone,"UTF-8");
                dialog.setMessage("로딩중");
                dialog.show();
                wait(3000);
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
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                //넘어오는 결과값을 저장할수 있도록 하는 메소드..
                InputStream inputStream = httpURLConnection.getInputStream();
                //해당 InputStream에 있는 값을 읽을 수 있도록 하기 위한 것
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                //bufferedReader로 읽어온 값을 temp에다 넣은 것이 null이 아니면 temp에다가 값을 직업넣고 개행을해준다.?
                while ((temp = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return  stringBuilder.toString().trim();

            }catch (Exception e){

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
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String courseProfessor;
                String courseTime;
                String courseTitle;

                int courseID;
                while (count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);
                    courseID = object.getInt("courseID");
                    courseProfessor = object.getString("courseProfessor");
                    courseTime = object.getString("courseTime");
                    courseTitle = object.getString("courseTitle");

                    schedule.addSchedule(courseTime, courseTitle, courseProfessor);
                    //adapter.notifyDataSetChanged();

                    count++;

                }

                dialog.dismiss();

            }catch (Exception e){
                e.printStackTrace();;
            }

            schedule.setting(monday, tuesday, wednesday, thursday, friday, getContext());

        }
    }

    // 단말기 핸드폰번호 얻어오기
    public String getPhoneNumber() {
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);


    }
}
