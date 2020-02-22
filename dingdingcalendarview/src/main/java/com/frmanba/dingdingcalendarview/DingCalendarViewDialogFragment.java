package com.frmanba.dingdingcalendarview;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frmanba.dingdingcalendarview.adpater.CalendarIntervalViewAdapter;
import com.frmanba.dingdingcalendarview.interf.OnSelectDateListener;
import com.frmanba.dingdingcalendarview.model.CalendarDate;
import com.frmanba.dingdingcalendarview.view.DingDayView;
import com.frmanba.dingdingcalendarview.widget.DatePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author mr.zang
 * date 2020-02-20
 * desc:
 */
public class DingCalendarViewDialogFragment extends DialogFragment implements View.OnClickListener {


    private View view;
    private TextView mTvSelDate;
    private View mVYearMonthIndicator;
    private RelativeLayout mReSelYearMonthWidget;
    private TextView mTvSelPm;
    private View mVTimeIndicator;
    private RelativeLayout mReTimeWidget;
    private TextView mTvOk;
    private ViewPager mViewPagerCalendar;
    private DatePickerView mHourV;
    private DatePickerView mMinuteV;
    private RelativeLayout mReHourWidget;
    private static final int MAX_MINUTE = 59;
    private static final int MAX_HOUR = 23;
    private static final int MIN_MINUTE = 0;
    private static final int MIN_HOUR = 0;

    private ArrayList<String> year, month, day, hour, minute;
    private int startHour, startMinute, endHour, endMinute;
    private boolean spanYear, spanMon, spanDay, spanHour, spanMin;
    private Calendar selectedCalender;
    private LinearLayout mLlYearMonth;
    private CalendarIntervalViewAdapter mCalendarViewAdapter;
    private OnSelTimeListener selTimeListener;

    public void setSelTimeListener(OnSelTimeListener selTimeListener) {
        this.selTimeListener = selTimeListener;
    }

    public enum SCROLL_TYPE {
        HOUR(1),
        MINUTE(2);

        SCROLL_TYPE(int value) {
            this.value = value;
        }

        public int value;
    }


    private int scrollUnits = SCROLL_TYPE.HOUR.value + SCROLL_TYPE.MINUTE.value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.dialog_fragment_ding_calendar_view, container, false);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.transparent);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
        initView(rootView);
        initCalendarView();
        initTime();
        return rootView;
    }

    private void initCalendarView() {
        DingDayView dingDayView = new DingDayView(getContext(), R.layout.widget_calendar_day_view);
        CalendarDate calendarDate = new CalendarDate();
        //        Calendar calendar = Calendar.getInstance();
        //        calendarDate.setYear(calendar.get(Calendar.YEAR));
        //        calendarDate.setMonth(calendar.get(Calendar.MONTH) - 1);
        //        calendarDate.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        mCalendarViewAdapter = new CalendarIntervalViewAdapter(getContext(),
                new OnSelectDateListener() {
                    @Override
                    public void onSelectDate(CalendarDate date) {
                        mTvSelDate.setText(date.toString());
                        selectedCalender.set(Calendar.YEAR, date.year);
                        selectedCalender.set(Calendar.MONTH, date.month - 1);
                        selectedCalender.set(Calendar.DAY_OF_MONTH, date.day);
                    }

                    @Override
                    public void onSelectOtherMonth(int offset) {

                    }
                }, CalendarAttr.CalendayType.MONTH,
                dingDayView, calendarDate, 3);
        initMonthPager();
    }

    private void initMonthPager() {
        mViewPagerCalendar.setAdapter(mCalendarViewAdapter);
        mViewPagerCalendar.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        mViewPagerCalendar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ArrayList<com.frmanba.dingdingcalendarview.view.Calendar> currentCalendars = mCalendarViewAdapter.getPagers();
                if (currentCalendars != null && currentCalendars.size() > 0) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    if (date != null)
                        mTvSelDate.setText(date.toString());
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPagerCalendar.setCurrentItem(1000);
    }

    private void initTime() {
        selectedCalender = Calendar.getInstance();
        spanHour = true;
        spanMin = true;
        addListener();
        initTimer();
    }

    private void initTimer() {
        initArrayList();
        startMinute = 0;
        startHour = 0;
        endHour = 23;
        endMinute = 59;

        if (spanHour) {
            if ((scrollUnits & SCROLL_TYPE.HOUR.value) != SCROLL_TYPE.HOUR.value) {
                hour.add(formatTimeUnit(startHour));
            } else {
                for (int i = startHour; i <= endHour; i++) {
                    hour.add(formatTimeUnit(i));
                }
            }

            if ((scrollUnits & SCROLL_TYPE.MINUTE.value) != SCROLL_TYPE.MINUTE.value) {
                minute.add(formatTimeUnit(startMinute));
            } else {
                for (int i = startMinute; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
        } else if (spanMin) {
            hour.add(formatTimeUnit(startHour));
            if ((scrollUnits & SCROLL_TYPE.MINUTE.value) != SCROLL_TYPE.MINUTE.value) {
                minute.add(formatTimeUnit(startMinute));
            } else {
                for (int i = startMinute; i <= endMinute; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
        }

        loadComponent();
    }

    private void loadComponent() {
        mHourV.setData(hour);
        mMinuteV.setData(minute);
        mHourV.setSelected(0);
        mMinuteV.setSelected(0);
        executeScroll();
    }

    private void executeScroll() {
        mHourV.setCanScroll(hour.size() > 1 && (scrollUnits & SCROLL_TYPE.HOUR.value) == SCROLL_TYPE.HOUR.value);
        mMinuteV.setCanScroll(minute.size() > 1 && (scrollUnits & SCROLL_TYPE.MINUTE.value) == SCROLL_TYPE.MINUTE.value);
    }

    private void initArrayList() {
        if (year == null) year = new ArrayList<>();
        if (month == null) month = new ArrayList<>();
        if (day == null) day = new ArrayList<>();
        if (hour == null) hour = new ArrayList<>();
        if (minute == null) minute = new ArrayList<>();
        year.clear();
        month.clear();
        day.clear();
        hour.clear();
        minute.clear();
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void addListener() {
        //选择小时
        mHourV.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                int num = Integer.parseInt(text);
                selectedCalender.set(Calendar.HOUR_OF_DAY, num);
                if (num > 12) {
                    mTvSelPm.setText("下午");
                } else {
                    mTvSelPm.setText("上午");
                }
                minuteChange();
            }
        });

        //选择分钟
        mMinuteV.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));
            }
        });
    }

    private void minuteChange() {
        if ((scrollUnits & SCROLL_TYPE.MINUTE.value) == SCROLL_TYPE.MINUTE.value) {
            minute.clear();
            int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
            if (selectedHour == startHour) {
                for (int i = startMinute; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            } else if (selectedHour == endHour) {
                for (int i = MIN_MINUTE; i <= endMinute; i++) {
                    minute.add(formatTimeUnit(i));
                }
            } else {
                for (int i = MIN_MINUTE; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
            selectedCalender.set(Calendar.MINUTE, Integer.parseInt(minute.get(0)));
            mMinuteV.setData(minute);
            mMinuteV.setSelected(0);
        }
        executeScroll();
    }

    private void initView(View rootView) {
        mTvSelDate = (TextView) rootView.findViewById(R.id.tv_sel_date);
        mVYearMonthIndicator = (View) rootView.findViewById(R.id.v_year_month_indicator);
        mReSelYearMonthWidget = (RelativeLayout) rootView.findViewById(R.id.re_sel_year_month_widget);
        mLlYearMonth = (LinearLayout) rootView.findViewById(R.id.ll_year_month);
        mTvSelPm = (TextView) rootView.findViewById(R.id.tv_sel_pm);
        mVTimeIndicator = (View) rootView.findViewById(R.id.v_time_indicator);
        mReTimeWidget = (RelativeLayout) rootView.findViewById(R.id.re_time_widget);
        mTvOk = (TextView) rootView.findViewById(R.id.tv_ok);
        mViewPagerCalendar = (ViewPager) rootView.findViewById(R.id.view_pager_calendar);
        mHourV = (DatePickerView) rootView.findViewById(R.id.hour_v);
        mMinuteV = (DatePickerView) rootView.findViewById(R.id.minute_v);
        mReHourWidget = (RelativeLayout) rootView.findViewById(R.id.re_hour_widget);
        mReSelYearMonthWidget.setOnClickListener(this);
        mReTimeWidget.setOnClickListener(this);
        mTvOk.setOnClickListener(this);

        mTvSelDate.setText("2020-02-20");
        mTvSelPm.setText("上午");
        mTvOk.setText("确定");

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.re_sel_year_month_widget) {//选择 yyyy-MM
            mVYearMonthIndicator.setVisibility(View.VISIBLE);
            mLlYearMonth.setVisibility(View.VISIBLE);
            mVTimeIndicator.setVisibility(View.INVISIBLE);
            mReHourWidget.setVisibility(View.INVISIBLE);
        } else if (id == R.id.re_time_widget) {
            selHour();
        } else if (id == R.id.tv_ok) {//确定
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
            String day = sdf.format(selectedCalender.getTime());
            String str = mTvSelPm.getText().toString();
            if (selTimeListener != null) {
                selTimeListener.selTimeCallBack(day + " " + str);
                dismiss();
            }
        }
    }

    public interface OnSelTimeListener {
        void selTimeCallBack(String date);
    }

    private void selHour() {
        //选择 hh:mm
        mVYearMonthIndicator.setVisibility(View.INVISIBLE);
        mLlYearMonth.setVisibility(View.INVISIBLE);
        mVTimeIndicator.setVisibility(View.VISIBLE);
        mReHourWidget.setVisibility(View.VISIBLE);
    }
}
