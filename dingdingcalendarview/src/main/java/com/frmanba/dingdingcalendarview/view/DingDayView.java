package com.frmanba.dingdingcalendarview.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.frmanba.dingdingcalendarview.R;
import com.frmanba.dingdingcalendarview.interf.IDayRenderer;
import com.frmanba.dingdingcalendarview.model.CalendarDate;

/**
 * @author mr.zang
 * date 2020-02-20
 * desc:
 */
public class DingDayView extends DayView {

    private View v;
    private TextView tv;

    /**
     * 构造器 传入资源文件创建DayView
     *
     * @param context        上下文
     * @param layoutResource 资源文件
     */
    public DingDayView(Context context, int layoutResource) {
        super(context, layoutResource);
        v = findViewById(R.id.v_background);
        tv = findViewById(R.id.date);
    }

    @Override
    public void refreshContent() {
        renderToday(day.getDate(), day.getState());
        renderSelect(day.getState());
        renderMarker(day.getDate(), day.getState());
        super.refreshContent();
    }

    private void renderMarker(CalendarDate date, State state) {
        v.setVisibility(VISIBLE);
        tv.setVisibility(VISIBLE);
    }

    private void renderSelect(State state) {
        if (state == State.SELECT) {
            v.setSelected(true);
            tv.setTextColor(Color.WHITE);
        } else if (state == State.NEXT_MONTH || state == State.PAST_MONTH) {
            v.setSelected(false);
            tv.setTextColor(Color.parseColor("#d5d5d5"));
        } else {
            v.setSelected(false);
            tv.setTextColor(Color.parseColor("#333333"));
        }
    }


    private void renderToday(CalendarDate date, State state) {
        if (date != null) {
            tv.setText(date.day + "");
            tv.setTextColor(Color.parseColor("#5AC46C"));
        }
    }

    @Override
    public IDayRenderer copy() {
        return new DingDayView(context, layoutResource);
    }
}
