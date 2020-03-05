package com.frmanba.dingdingcalendarview.adpater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.frmanba.dingdingcalendarview.CalendarAttr;
import com.frmanba.dingdingcalendarview.Utils;
import com.frmanba.dingdingcalendarview.interf.IDayRenderer;
import com.frmanba.dingdingcalendarview.interf.OnAdapterSelectListener;
import com.frmanba.dingdingcalendarview.interf.OnSelectDateListener;
import com.frmanba.dingdingcalendarview.model.CalendarDate;
import com.frmanba.dingdingcalendarview.view.Calendar;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarIntervalViewAdapter extends PagerAdapter {

    /**
     * 周排列方式
     * 1：代表周日显示为本周的第一天 默认
     * 0:代表周一显示为本周的第一天
     */
    public static int weekArrayType = 1;

    /**
     * 日历的天
     */
    private static CalendarDate date = new CalendarDate();

    /**
     * 存储日历的集合
     */
    private ArrayList<Calendar> calendars = new ArrayList<>();

    private int currentPosition;

    private CalendarAttr.CalendayType calendarType = CalendarAttr.CalendayType.MONTH;

    private int rowCount = 0;

    private CalendarDate seedDate;


    public CalendarIntervalViewAdapter(Context context, OnSelectDateListener onSelectDateListener,
                                       CalendarAttr.CalendayType calendarType, IDayRenderer dayView,
                                       CalendarDate calendarDate, int calendarNum) {
        this.calendarType = calendarType;
        init(context, onSelectDateListener, calendarDate, calendarNum);
        setCustomDayRenderer(dayView, calendarNum);
    }

    private void init(Context context, OnSelectDateListener onSelectDateListener, CalendarDate calendarDate, int calendarNum) {
        saveDate(calendarDate);
        //初始化的种子日期为今天
        seedDate = calendarDate.modifyDay(1);
        for (int i = 0; i < 3; i++) {
            Calendar calendar = new Calendar(context, onSelectDateListener);
            calendar.setOnAdapterSelectListener(new OnAdapterSelectListener() {
                @Override
                public void cancelSelectState() {
                    cancelOtherSelectState();
                }

                @Override
                public void updateSelectState() {
                    invalidateCurrentCalendar();
                }
            });
            calendars.add(calendar);
        }
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        this.currentPosition = position;
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (position < 2) {
            return null;
        }
        Calendar calendar = calendars.get(position % calendars.size());
        if (calendarType == CalendarAttr.CalendayType.MONTH) {
            CalendarDate current = seedDate.modifyMonth(position - 1000);
            current.setDay(1);//每月的种子日期都是1号
            calendar.showDate(current);
        } else {
            CalendarDate current = seedDate.modifyWeek(position - 1000);
            if (weekArrayType == 1) {
                calendar.showDate(Utils.getSaturday(current));
            } else {
                calendar.showDate(Utils.getSunday(current));
            }//每周的种子日期为这一周的最后一天
            calendar.updateWeek(rowCount);
        }
        if (container.getChildCount() == calendars.size()) {
            container.removeView(calendars.get(position % 3));
        }
        if (container.getChildCount() < calendars.size()) {
            container.addView(calendar, 0);
        } else {
            container.addView(calendar, position % 3);
        }
        return calendar;
    }


    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(container);
    }

    public ArrayList<Calendar> getPagers() {
        return calendars;
    }

    public void cancelOtherSelectState() {
        for (int i = 0; i < calendars.size(); i++) {
            Calendar calendar = calendars.get(i);
            calendar.cancelSelectState();
        }
    }

    public void invalidateCurrentCalendar() {
        for (int i = 0; i < calendars.size(); i++) {
            Calendar calendar = calendars.get(i);
            calendar.update();
            if (calendar.getCalendarType() == CalendarAttr.CalendayType.WEEK) {
                calendar.updateWeek(rowCount);
            }
        }
    }

    public void setMarkData(HashMap<String, String> markData) {
        Utils.setMarkData(markData);
    }

    public void setMarkDataChange(HashMap<String, String> markData) {
        Utils.setMarkData(markData);
        notifyDataSetChanged();
    }

    public void setMarkProgressChange(HashMap<String, Float> markData) {
        Utils.setMarkProgress(markData);
        notifyDataSetChanged();
    }

    /**
     * 选择月份
     */
    public void switchToMonth() {
        if (calendars != null && calendars.size() > 0 && calendarType != CalendarAttr.CalendayType.MONTH) {
            calendarType = CalendarAttr.CalendayType.MONTH;
            //            MonthPager.CURRENT_DAY_INDEX = currentPosition;
            Calendar v = calendars.get(currentPosition % 3);//0
            seedDate = v.getSeedDate();

            Calendar v1 = calendars.get(currentPosition % 3);//0
            v1.switchCalendarType(CalendarAttr.CalendayType.MONTH);
            v1.showDate(seedDate);

            Calendar v2 = calendars.get((currentPosition - 1) % 3);//2
            v2.switchCalendarType(CalendarAttr.CalendayType.MONTH);
            CalendarDate last = seedDate.modifyMonth(-1);
            last.setDay(1);
            v2.showDate(last);

            Calendar v3 = calendars.get((currentPosition + 1) % 3);//1
            v3.switchCalendarType(CalendarAttr.CalendayType.MONTH);
            CalendarDate next = seedDate.modifyMonth(1);
            next.setDay(1);
            v3.showDate(next);
        }
    }

    /**
     * 选择星期
     *
     * @param rowIndex 下标
     */
    public void switchToWeek(int rowIndex) {
        rowCount = rowIndex;
        if (calendars != null && calendars.size() > 0 && calendarType != CalendarAttr.CalendayType.WEEK) {
            calendarType = CalendarAttr.CalendayType.WEEK;
            //MonthPager.CURRENT_DAY_INDEX = currentPosition;
            Calendar v = calendars.get(currentPosition % 3);
            seedDate = v.getSeedDate();

            rowCount = v.getSelectedRowIndex();

            Calendar v1 = calendars.get(currentPosition % 3);
            v1.switchCalendarType(CalendarAttr.CalendayType.WEEK);
            v1.showDate(seedDate);
            v1.updateWeek(rowIndex);

            Calendar v2 = calendars.get((currentPosition - 1) % 3);
            v2.switchCalendarType(CalendarAttr.CalendayType.WEEK);
            CalendarDate last = seedDate.modifyWeek(-1);
            if (weekArrayType == 1) {
                v2.showDate(Utils.getSaturday(last));
            } else {
                v2.showDate(Utils.getSunday(last));
            }//每周的种子日期为这一周的最后一天
            v2.updateWeek(rowIndex);

            Calendar v3 = calendars.get((currentPosition + 1) % 3);
            v3.switchCalendarType(CalendarAttr.CalendayType.WEEK);
            CalendarDate next = seedDate.modifyWeek(1);
            if (weekArrayType == 1) {
                v3.showDate(Utils.getSaturday(next));
            } else {
                v3.showDate(Utils.getSunday(next));
            }//每周的种子日期为这一周的最后一天
            v3.updateWeek(rowIndex);
        }
    }

    public void notifyDataChanged(CalendarDate date) {
        seedDate = date;
        saveDate(date);
        if (calendarType == CalendarAttr.CalendayType.WEEK) {
            //  MonthPager.CURRENT_DAY_INDEX = currentPosition;
            Calendar v1 = calendars.get(currentPosition % 3);
            v1.showDate(seedDate);
            v1.updateWeek(rowCount);

            Calendar v2 = calendars.get((currentPosition - 1) % 3);
            CalendarDate last = seedDate.modifyWeek(-1);
            if (weekArrayType == 1) {
                v2.showDate(Utils.getSaturday(last));
            } else {
                v2.showDate(Utils.getSunday(last));
            }//每周的种子日期为这一周的最后一天
            v2.updateWeek(rowCount);

            Calendar v3 = calendars.get((currentPosition + 1) % 3);
            CalendarDate next = seedDate.modifyWeek(1);
            if (weekArrayType == 1) {
                v3.showDate(Utils.getSaturday(next));
            } else {
                v3.showDate(Utils.getSunday(next));
            }//每周的种子日期为这一周的最后一天
            v3.updateWeek(rowCount);
        } else {
            // MonthPager.CURRENT_DAY_INDEX = currentPosition;
            Calendar v1 = calendars.get(currentPosition % 3);//0
            v1.showDate(seedDate);
            Calendar v2 = calendars.get((currentPosition - 1) % 3);//2
            CalendarDate last = seedDate.modifyMonth(-1);
            last.setDay(1);
            v2.showDate(last);
            Calendar v3 = calendars.get((currentPosition + 1) % 3);//1
            CalendarDate next = seedDate.modifyMonth(1);
            next.setDay(1);
            v3.showDate(next);
        }
    }

    public static void saveDate(CalendarDate calendarDate) {
        date = calendarDate;
    }

    public static CalendarDate loadDate() {
        return date;
    }

    public CalendarAttr.CalendayType getCalendarType() {
        return calendarType;
    }

    /**
     * 为每一个Calendar实例设置renderer对象
     *
     * @return void
     */
    public void setCustomDayRenderer(IDayRenderer dayRenderer, int calendarNum) {
        if (calendarNum == 1) {
            Calendar c0 = calendars.get(0);
            c0.setDayRenderer(dayRenderer);
        } else if (calendarNum == 2) {
            Calendar c0 = calendars.get(0);
            c0.setDayRenderer(dayRenderer);
            Calendar c1 = calendars.get(1);
            c1.setDayRenderer(dayRenderer.copy());
        } else if (calendarNum == 3) {
            Calendar c0 = calendars.get(0);
            c0.setDayRenderer(dayRenderer);
            Calendar c1 = calendars.get(1);
            c1.setDayRenderer(dayRenderer.copy());
            Calendar c2 = calendars.get(2);
            c2.setDayRenderer(dayRenderer.copy());
        }
    }
}