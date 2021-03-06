package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myapplication.util.PatternLockUtils;
import com.github.ajalt.reprint.core.Reprint;

import java.util.ArrayList;

public class Setting extends BaseActivity {

    private ArrayList<Setting_item> data;
    private ListView lv;
    static Setting_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        data = new ArrayList<Setting_item>();

        // String s = "1";
        //타이틀바 삭제
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d("1", "1");
        MyAsyncTask asyncTask = new MyAsyncTask();
        Log.d("2", "2");
        asyncTask.execute(data);

        Log.d("3", "3");
    }

    public class MyAsyncTask extends AsyncTask<ArrayList<Setting_item>, ArrayList<Setting_item>, ArrayList<Setting_item>> {
        //doInbackground 메소드가 실행되기전 실행되는 메소드이다. 비동기 처리전에 무엇인가 처리를 하고 싶다면 사용
        protected void onPreExecute() {
        }

        // 처리하고 싶은 내용
        protected ArrayList<Setting_item> doInBackground(ArrayList<Setting_item>... params) {
            Log.d("4", "4");
            data = params[0];
            try {
                System.out.print("kkkkkkkkkk");
                Reprint.initialize(getApplicationContext());

                data.add(new Setting_item("잠금방식 및 NFC", "지문,패턴,NFC"));

                if (Reprint.hasFingerprintRegistered()) {
                    data.add(new Setting_item("지문 인식", "등록된 지문이 있습니다"));
                } else {
                    data.add(new Setting_item("지문 인식", "등록된 지문이 없습니다."));
                }

                if (PatternLockUtils.hasPattern(getApplicationContext())) {
                    data.add(new Setting_item("패턴 방식", "등록된 패턴이 있습니다."));
                } else {
                    data.add(new Setting_item("패턴 방식", "등록된 패턴이 없습니다."));
                }

                adapter = new Setting_adapter(getApplicationContext(), R.layout.activity_setting_item, data);

                lv = (ListView) findViewById(R.id.setting_listView);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("5", "5");
            return null;
        }

        // 비동기 처리의 진행 상황을 진행률로 표시하고 싶을 때 등 에서 사용, 백그라운드에 호출되는 경우 처리된다.
        protected void onProgressUpdate() {

        }

        // doInBackground 메소드 후에 실행되는 마지막 메소드이다. 백그라운드 메소드의 반환값을 인자로 받아 그결과를 화면에 반영 할 수 있다.
        protected void onPostExecute(ArrayList<Setting_item> s) {
            Log.d("6", "6");
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    // 리스트뷰의 아이템 (한 행)이 클릭된 경우를 처리
                    // 클릭이 되었을 때 넘어오는 패러미터 중 position이라는 이름을 가진 int타입의 변수는
                    // 리스트뷰에서 몇번째 아이템이 클릭되었는지 숫자로 나타냄
                    Intent intent;
                    switch (position) {
                        //잠금방식
                        case 0: // position의 값이 0일 경우 = 맨 처음꺼j
                            // 인텐트에 값을 실어 보냄
                            intent = new Intent(getApplicationContext(), SetLockActivity.class);
                            startActivity(intent);
                            break;
                        //지문인식
                        case 1:
                            intent = new Intent(getApplicationContext(), SetFPActivity.class);
                            startActivity(intent);

                            break;
                        //패턴방식
                        case 2:
                            intent = new Intent(getApplicationContext(), ConfirmPatternActivity.class);
                            intent.putExtra("preActivity", "okokok");
                            startActivity(intent);
                            break;
                        //position의 값이 위에서 지정한 case에 하나도 맞지 않을경우
                        default:
                            //실행코드
                            break;
                    }
                }
            });
        }
    }
}