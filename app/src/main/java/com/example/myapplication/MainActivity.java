package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.util.PatternLockUtils;
import com.github.ajalt.reprint.core.Reprint;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

//mm
public class MainActivity extends AppCompatActivity {
    String host = "223.194.158.91";    // 서버 컴퓨터 IP
    //String host = "121.161.183.214";
    int port = 5001;
    FirstConnectThread thread;
    Bundle bundle;
    Handler mHandler;
    String storage;

    long now;
    Date date;
    SimpleDateFormat sdfNow;
    String formatDate;
    TextView dateNow;
    private SharedPreferences pref;
    //※※※※※※ 신규, 기존 회원 구별
   // private SharedPreferences memberPref;
  //  int loginNum = 0;
    //※※※※※※
    private TelephonyManager telephonyManager;
    private EditText Stuid;
    Intent intent;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this); //뒤로버튼 종료

        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);


        String state = Environment.getExternalStorageState();
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE
            }, 466);
        }

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 466);
        }

        now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        formatDate = sdfNow.format(date);

        dateNow = (TextView) findViewById(R.id.qr_date);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        Button btn = (Button) findViewById(R.id.login_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //intent = new Intent(getApplicationContext(),NewFirstView.class);
                //startActivity(intent);
                thread = new FirstConnectThread();
                thread.start();
            }
        });


        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                bundle = msg.getData();

                String ss = bundle.getString("key");

                Toast.makeText(getApplicationContext(), bundle.getString("key"), Toast.LENGTH_SHORT).show();
            }
        };
    }

    class FirstConnectThread extends Thread {
        int port = 80;
        Object input;
        String output_id;
        String output_pw;
        String output_num;
        SharedPreferences id_pref;
        SharedPreferences.Editor id_commit;

        SharedPreferences storage_pref;
        SharedPreferences.Editor storage_commit;

        //ProgressBar progressBar = (ProgressBar)findViewById(R.id.qr_bar);
        public void run() {

            String host = "113.198.84.23";

         //   memberPref = getSharedPreferences("memberPref", MODE_PRIVATE);
         //   final SharedPreferences.Editor memberEditor = memberPref.edit();
            try {
                Socket socket = new Socket(host, port);
                System.out.println("서버로 연결되었습니다. : " + host + ", " + port);
                //Toast.makeText(MainActivity.this, "connect server : " + host + ", " + port , Toast.LENGTH_SHORT).show();
                telephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                String output_num = telephonyManager.getLine1Number();
                output_num = output_num.replace("+82", "0");
                EditText id = (EditText) findViewById(R.id.login_id_et);
                EditText password = (EditText) findViewById(R.id.login_pw_et);

                //String output = m_etSendData.getText().toString();
                output_id = id.getText().toString();
                ;
                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject(output_id);
                outstream.flush();
                System.out.println("서버로 보낸 데이터 : " + output_id);
                //Toast.makeText(MainActivity.this, "서버로 보낸 데이터 : " + output , Toast.LENGTH_SHORT).show();

                output_pw = password.getText().toString() + "%3B%3B" + output_num;
                //output_pw = formatDate;
                ObjectOutputStream outstream2 = new ObjectOutputStream(socket.getOutputStream());
                outstream2.writeObject(output_pw);
                outstream2.flush();
                System.out.println("서버로 보낸 데이터 : " + output_pw);

                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                //Bitmap bitmap = (Bitmap)instream.readObject();
                input = instream.readObject();
                //System.out.println(instream.readObject());
                System.out.println("서버로부터 받은 데이터: " + input);


                //Toast.makeText(getApplicationContext(), "서버로부터 받은 데이터 : " + input , Toast.LENGTH_SHORT).show();

                /*
                // 서버에서 허가를 받으면 다음 화면으로
                if(input.toString().equals("허가")){
                    Intent intent = new Intent(getApplicationContext(),NewFirstView.class);
                    startActivity(intent);
                }
                */

                if (input.toString().equals("허가")) {
                    //intent.putExtra("pid", id.getText().toString());
                    String pid = id.getText().toString();
                    id_pref = getSharedPreferences("login", MODE_APPEND);
                    id_commit = id_pref.edit();
                    id_commit.putString("id", pid);
                    id_commit.commit();
                    System.out.println(">>>>>>>>>>>>>>>> : " + id_pref.getString("id", ""));



                    /*/※※※※※※ 신규, 기존 회원 구별

                    int loginNum2 = memberPref.getInt("loginNum", 0);
                    Intent intent;
                    loginNum += 1;
                    if (loginNum2 < 1) {
                        intent = new Intent(getApplicationContext(), NewFirstView.class);
                    } else intent = new Intent(getApplicationContext(), OldFirstView.class);
                    memberEditor.putInt("loginNum", loginNum);
                    memberEditor.commit();


                    //※※※※※※*/

                    Reprint.initialize(getApplicationContext());
                    //지문이 단말기에 등록되어있는지 ||  패턴이 등록되어있는지 확인하는 조건문!!
                    if (PatternLockUtils.hasPattern(getApplicationContext()))
                    {
                        intent = new Intent(getApplicationContext(), OldFirstView.class);
                    }
                    else
                        intent = new Intent(getApplicationContext(), NewFirstView.class);

                    startActivity(intent);

                } else if (input.toString().equals("불허가")) {
                    bundle = new Bundle();
                    bundle.putString("key", "ID와 PW를 다시 확인해주세요");
                    Message msg = new Message();
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }

                instream.close();
                outstream.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("접근실패");
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
        /*
        am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        am.restartPackage(getPackageName());*/
    }

}