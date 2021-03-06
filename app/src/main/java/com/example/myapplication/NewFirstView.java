package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class NewFirstView extends BaseActivity {

    private Button btn;
    private Button btn2;
    private Intent settingintent;
    private BackPressCloseHandler backPressCloseHandler;
    ImgConnectThread imgthread;

    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_first_view);
        pref = getSharedPreferences("pref", MODE_PRIVATE); // Shared Preference를 불러옵니다.
        SharedPreferences.Editor editor = pref.edit(); // Editor를 불러옵니다.

        // 저장할 값들을 입력합니다.
        editor.putBoolean("FP", false);
        editor.putBoolean("PT", true);


        imgthread = new ImgConnectThread();
        imgthread.start();

        backPressCloseHandler = new BackPressCloseHandler(this); //뒤로버튼 종료
        btn = (Button) findViewById(R.id.nfv_register_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetPatternActivity.class);
                intent.putExtra("preSetPattern","NewFirstView");
                startActivity(intent);
            }
            //Intent mainintent = getIntent();
            //String pid = mainintent.getStringExtra("pid");
            // Intent intent = new Intent(getApplicationContext(), OldFirstView.class);
            //intent.putExtra("pid", pid);
            //startActivity(intent);

        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    class ImgConnectThread extends Thread {
        String host = "113.198.84.29";
        int port = 80;

        Object input;
        String storage;

        String output_id;
        String output_pw;

        SharedPreferences storage_pref;
        SharedPreferences.Editor storage_commit;

        public void run() {


            try {
                Socket socket = new Socket(host, port);
                System.out.println("서버로 연결되었습니다. : " + host + ", " + port);
                storage = "imgcall";
                storage_pref = getSharedPreferences("login", MODE_APPEND);
                output_id = storage_pref.getString("id", "");


                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject(storage);
                outstream.flush();
                System.out.println("서버로 보낸 데이터 : " + storage);

                ObjectOutputStream outstream2 = new ObjectOutputStream(socket.getOutputStream());
                outstream2.writeObject(output_id);
                outstream2.flush();
                System.out.println("서버로 보낸 데이터2 : " + output_id);


                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                input = instream.readObject();
                System.out.println("서버로부터 받은 데이터: " + input);


                //이미지 파일 읽어들이는 부분
                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                DataInputStream dis = new DataInputStream(bis);

                int filesCount = dis.readInt();  //파일 갯수 읽음
                System.out.println("1-1 filescount : " + filesCount);
                File[] files = new File[filesCount]; // 파일을 read한 것 받아 놓습니다.
                System.out.println("1-2 for문 시작전 : ");
                for (int i = 0; i < filesCount; i++) {   //파일 갯수 만큼 for문 돕니다.
                    System.out.println("1-3 for들어옴 : ");
                    long fileLength = dis.readLong();    //파일 길이 받습니다.
                    String fileName = dis.readUTF();     //파일 이름 받습니다.

                    System.out.println("수신 파일 이름 : " + fileName);

                    files[i] = new File(fileName);
                    System.out.println("1-4 파일 저장? : ");
                    FileOutputStream fos = openFileOutput(files[i].getName(), Context.MODE_PRIVATE);
                    // FileOutputStream fos = new FileOutputStream(files[i]); // 읽은 파일들 폰에서 지정한 폴더로 내보냅니다.
                    System.out.println("1-5 파일 지정폴더로 보냄? : ");
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    System.out.println("1-6 파일복사 저장 for문 전:");
                    for (int j = 0; j < fileLength; j++) //파일 길이 만큼 읽습니다.
                        bos.write(bis.read());
                    System.out.println("1-7 파일 복사 성공? : ");
                    bos.flush();

                    storage = getFilesDir().toString();
                    System.out.println("1-8 파일 위치 : " + storage);
                    storage = storage + "/" + output_id + ".jpg";           // input은 보낸 학번값

                    //이미지 경로 저장
                    storage_pref = getSharedPreferences("storage", MODE_APPEND);
                    storage_commit = storage_pref.edit();
                    storage_commit.putString("storage", storage);
                    storage_commit.commit();
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
}
