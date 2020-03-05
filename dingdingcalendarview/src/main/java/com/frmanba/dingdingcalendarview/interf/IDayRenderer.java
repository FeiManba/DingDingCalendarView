package com.frmanba.dingdingcalendarview.interf;

import android.graphics.Canvas;

import com.frmanba.dingdingcalendarview.view.Day;

/**
 * Created by ldf on 17/6/26.
 */

public interface IDayRenderer {

    void refreshContent();

    void drawDay(Canvas canvas, Day day);

    IDayRenderer copy();

}
