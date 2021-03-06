package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;

public class Finger extends BaseActivity {
    private ImageView img;
    private Handler mHandler;
    private Bundle bundle;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        intent = new Intent(getApplicationContext(), QRcode.class);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                bundle = msg.getData();

                String ss = bundle.getString("key");
                intent.putExtra("finger", ss);
            }
        };

        img = (ImageView) findViewById(R.id.imageView);
        GlideDrawableImageViewTarget gifimg = new GlideDrawableImageViewTarget(img);
        Glide.with(this).load(R.drawable.touchid2).into(gifimg);

        Reprint.initialize(this);

        if (!Reprint.hasFingerprintRegistered()) {
            img.setImageResource(R.drawable.failure);
            Toast.makeText(getApplicationContext(), "이 장치에 등록 된 지문이 없습니다.", Toast.LENGTH_LONG).show();
        } else {
            Reprint.authenticate(new AuthenticationListener() {
                @Override
                public void onSuccess(int moduleTag) {

                    intent = new Intent(getApplicationContext(), QRcode.class);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }

                @Override
                public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
                    img.setImageResource(R.drawable.failure);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Reprint.cancelAuthentication();
    }

    private boolean checkDeviceSpec() {
        boolean fingerprintFlag = Reprint.isHardwarePresent();
        boolean hasRegisteredFlag = Reprint.hasFingerprintRegistered();
        return fingerprintFlag && hasRegisteredFlag;
    }
}