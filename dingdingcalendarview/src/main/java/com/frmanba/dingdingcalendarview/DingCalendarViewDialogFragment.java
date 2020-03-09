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
    private DatePickerView mPickerPm;
    private RelativeLayout mReHourWidget;
    private ArrayList<String> pm;
    private int startPm, endPm;
    private boolean spanPm;
    private Calendar selectedCalender;
    private LinearLayout mLlYearMonth;
    private CalendarIntervalViewAdapter mCalendarViewAdapter;
    private OnSelTimeListener selTimeListener;
    private View mVWidget;
    //0 上午 1 下午
    private int typePm = 0;

    public void setTypePm(int typePm) {
        this.typePm = typePm;
    }

    public void setSelectedCalender(Calendar selectedCalender) {
        this.selectedCalender = selectedCalender;
    }

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
        calendarDate.setYear(selectedCalender.get(Calendar.YEAR));
        calendarDate.setMonth(selectedCalender.get(Calendar.MONTH) + 1);
        calendarDate.setDay(selectedCalender.get(Calendar.DAY_OF_MONTH));
        mCalendarViewAdapter = new CalendarIntervalViewAdapter(getContext(),
                new OnSelectDateListener() {
                    @Override
                    public void onSelectDate(CalendarDate date) {
                        mTvSelDate.setText(date.toString());
                        selectedCalender.set(Calendar.YEAR, date.year);
                        selectedCalender.set(Calendar.MONTH, date.month - 1);
                        selectedCalender.set(Calendar.DAY_OF_MONTH, date.day);
                        selHour();
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
        spanPm = true;
        addListener();
        initTimer();
    }

    private void initTimer() {
        initArrayList();
        startPm = 0;
        endPm = 1;
        if (spanPm) {
            pm.add("上午");
            pm.add("下午");
        }
        loadComponent();
    }

    private void loadComponent() {
        mPickerPm.setData(pm);
        mPickerPm.setSelected(typePm == 0 ? 0 : 1);
        executeScroll();
    }

    private void executeScroll() {
        mPickerPm.setCanScroll(pm.size() > 1);
    }

    private void initArrayList() {
        if (pm == null) pm = new ArrayList<>();
        pm.clear();
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void addListener() {
        //选择小时
        mPickerPm.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mTvSelPm.setText(text);
            }
        });
    }

    private void initView(View rootView) {
        mVWidget = (View) rootView.findViewById(R.id.v_widget);
        mVWidget.setOnClickListener(this);
        mTvSelDate = (TextView) rootView.findViewById(R.id.tv_sel_date);
        mVYearMonthIndicator = (View) rootView.findViewById(R.id.v_year_month_indicator);
        mReSelYearMonthWidget = (RelativeLayout) rootView.findViewById(R.id.re_sel_year_month_widget);
        mLlYearMonth = (LinearLayout) rootView.findViewById(R.id.ll_year_month);
        mTvSelPm = (TextView) rootView.findViewById(R.id.tv_sel_pm);
        mVTimeIndicator = (View) rootView.findViewById(R.id.v_time_indicator);
        mReTimeWidget = (RelativeLayout) rootView.findViewById(R.id.re_time_widget);
        mTvOk = (TextView) rootView.findViewById(R.id.tv_ok);
        mViewPagerCalendar = (ViewPager) rootView.findViewById(R.id.view_pager_calendar);
        mPickerPm = (DatePickerView) rootView.findViewById(R.id.picker_am);
        mReHourWidget = (RelativeLayout) rootView.findViewById(R.id.re_hour_widget);
        mReSelYearMonthWidget.setOnClickListener(this);
        mReTimeWidget.setOnClickListener(this);
        mTvOk.setOnClickListener(this);
        if (selectedCalender == null) {
            selectedCalender = Calendar.getInstance();
        }
        int year = selectedCalender.get(Calendar.YEAR);
        int month = selectedCalender.get(Calendar.MONTH) + 1;
        int day = selectedCalender.get(Calendar.DAY_OF_MONTH);
        String selDate = year + "-" + month + "-" + day;
        mTvSelDate.setText(selDate);
        mTvSelPm.setText(typePm == 1 ? "下午" : "上午");
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
            String pm = mTvSelPm.getText().toString();
            if (selTimeListener != null) {
                selTimeListener.selTimeCallBack(day, pm.equals("下午") ? 1 : 0);
                dismiss();
            }
        } else if (id == R.id.v_widget) {
            this.dismiss();
        }
    }

    public interface OnSelTimeListener {
        /**
         * @param date   yyyy年MM月dd日
         * @param typePm 0 上午 1 下午
         */
        void selTimeCallBack(String date, int typePm);
    }

    private void selHour() {
        //选择 hh:mm
        mVYearMonthIndicator.setVisibility(View.INVISIBLE);
        mLlYearMonth.setVisibility(View.INVISIBLE);
        mVTimeIndicator.setVisibility(View.VISIBLE);
        mReHourWidget.setVisibility(View.VISIBLE);
    }
}
