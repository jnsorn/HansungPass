package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapplication.patternlock.PatternView;
import com.example.myapplication.util.AppUtils;
import com.example.myapplication.util.PatternLockUtils;
import com.example.myapplication.util.ThemeUtils;

import java.util.List;

public class SetPatternActivity extends com.example.myapplication.patternlock.SetPatternActivity {

    Intent preIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        AppUtils.setActionBarDisplayUp(this);
        preIntent = getIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppUtils.navigateUp(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        PatternLockUtils.setPattern(pattern, this);
    }
            //    intent.putExtra("preSetPattern","ConfirmPatternActivity");                intent.putExtra("preSetPattern","NewFirstView");

    @Override
    protected void onStop() {
        Intent intent;
        if (PatternLockUtils.hasPattern(getApplicationContext())&& preIntent.getExtras().getString("preSetPattern").equals("NewFirstView")) {
            intent = new Intent(getApplicationContext(), OldFirstView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Toast.makeText(getApplicationContext(),"등록에 성공했습니다.",Toast.LENGTH_LONG).show();
            startActivity(intent);
        } else if(!PatternLockUtils.hasPattern(getApplicationContext())){
             Toast.makeText(getApplicationContext(),"등록에 실패했습니다.\n다시 등록해주세요.",Toast.LENGTH_LONG).show();
        }
        super.onStop();
    }


}
