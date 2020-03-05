package com.frmanba.dingdingcalendarview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.frmanba.dingdingcalendarview.Utils;
import com.frmanba.dingdingcalendarview.interf.IDayRenderer;


/**
 * Created by ldf on 16/10/19.
 */

public abstract class DayView extends RelativeLayout implements IDayRenderer {

    protected Day day;
    protected Context context;
    protected int layoutResource;
    private int mChildMeasureWidth;
    private int mChildMeasureHeight;

    /**
     * 构造器 传入资源文件创建DayView
     *
     * @param layoutResource 资源文件
     * @param context        上下文
     */
    public DayView(Context context, int layoutResource) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setupLayoutResource(layoutResource);
        this.context = context;
        this.layoutResource = layoutResource;
    }

    /**
     * 为自定义的DayView设置资源文件
     *
     * @param layoutResource 资源文件
     * @return CalendarDate 修改后的日期
     */
    private void setupLayoutResource(int layoutResource) {
        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        inflated.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(Utils.dpi2px(getContext(), 51), Utils.dpi2px(getContext(), 45));
            child.setLayoutParams(lp);
        }
        //headerView的宽度信息
        mChildMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        if (lp.height > 0) {
            mChildMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            mChildMeasureHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);//未指定
        }
        //将宽和高设置给child
        child.measure(mChildMeasureWidth, mChildMeasureHeight);
        child.layout(0, 0, mChildMeasureWidth, mChildMeasureHeight);
    }

    @Override
    public void refreshContent() {
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    public void drawDay(Canvas canvas, Day day) {
        this.day = day;
        refreshContent();
        int saveId = canvas.save();
        canvas.translate(getTranslateX(canvas, day),
                day.getPosRow() * getMeasuredHeight());
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    private int getTranslateX(Canvas canvas, Day day) {
        int dx;
        int canvasWidth = canvas.getWidth() / 7;
        int viewWidth = getMeasuredWidth();
        int moveX = (canvasWidth - viewWidth) / 2;
        dx = day.getPosCol() * canvasWidth + moveX;
        return dx;
    }
}