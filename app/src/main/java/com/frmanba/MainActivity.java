package com.frmanba;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.frmanba.dingdingcalendarview.DingCalendarViewDialogFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 钉钉请假选择日期
     */
    private Button mBtnDingDingCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnDingDingCalendar = (Button) findViewById(R.id.btn_ding_ding_calendar);
        mBtnDingDingCalendar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_ding_ding_calendar:
                //钉钉请假选择日期
                DingCalendarViewDialogFragment dingCalendarViewDialogFragment = new DingCalendarViewDialogFragment();
                Calendar instance = Calendar.getInstance();
                dingCalendarViewDialogFragment.setSelectedCalender(instance);
                dingCalendarViewDialogFragment.setTypePm(1);
                dingCalendarViewDialogFragment.setSelTimeListener(new DingCalendarViewDialogFragment.OnSelTimeListener() {
                    /**
                     * @param date   yyyy年MM月dd日
                     * @param typePm 0 上午 1 下午
                     */
                    @Override
                    public void selTimeCallBack(String date, int typePm) {
                        String str = String.valueOf(typePm);
                        Toast.makeText(MainActivity.this, date + "  " + str, Toast.LENGTH_SHORT).show();
                    }
                });
                dingCalendarViewDialogFragment.show(getSupportFragmentManager(), "dingCalendarViewDialogFragment");
                break;
        }
    }
}
