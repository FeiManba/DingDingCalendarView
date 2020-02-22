package com.frmanba.dingdingcalendarview.adpater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author mr.zang
 * date 2020-02-20
 * desc:
 */
public class CalendarDayViewAdapter extends BaseAdapter<String, CalendarDayViewAdapter.ViewHolder> {

    public CalendarDayViewAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void onBindDataViewHolder(ViewHolder holder, int position, String s) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
