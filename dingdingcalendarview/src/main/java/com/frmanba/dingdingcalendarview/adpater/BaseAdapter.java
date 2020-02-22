package com.frmanba.dingdingcalendarview.adpater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class BaseAdapter<D extends Object, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public Context mContext;
    public ArrayList<D> mds;

    public ArrayList<D> getMds() {
        return mds;
    }

    public BaseAdapter(Context context) {
        mContext = context;
        this.mds = new ArrayList<>();
    }

    public BaseAdapter(Context context, ArrayList<D> mds) {
        mContext = context;
        this.mds = mds;
    }


    @Override
    @NonNull
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateChildViewHolder(parent, viewType);
    }

    protected abstract VH onCreateChildViewHolder(ViewGroup parent, int viewType);

    public void initMDatas(ArrayList<D> ds) {
        if (ds == null) return;
        if (ds.size() == 0) return;
        mds.clear();
        mds.addAll(ds);
        notifyDataSetChanged();
    }

    public void initMDatasToOne(ArrayList<D> ds) {
        if (ds == null) return;
        if (ds.size() == 0) return;
        mds.clear();
        mds.addAll(0, ds);
        notifyDataSetChanged();
    }


    public void addDataModelIndex(D d, int index) {
        if (d == null) return;
        if (index < 0) {
            index = 1;
        } else if (index >= mds.size()) {
            index = mds.size();
        }
        mds.add(index, d);
        notifyDataSetChanged();
    }

    public void initMData(D d) {
        if (d == null) return;
        mds.clear();
        mds.add(d);
        notifyDataSetChanged();
    }

    public void initMDataToOne(D d) {
        if (d == null) return;
        mds.add(0, d);
        notifyDataSetChanged();
    }

    public void addDataModel(ArrayList<D> ds) {
        if (ds == null || ds.size() == 0) return;
        mds.addAll(ds);
        notifyDataSetChanged();
    }


    public void addDataModelOne(D ds) {
        if (ds == null) return;
        mds.add(0, ds);
        notifyDataSetChanged();
    }

    public void addStartDataModel(ArrayList<D> ds) {
        if (ds == null || ds.size() == 0) return;
        mds.addAll(0, ds);
        notifyDataSetChanged();
    }

    public void addDataModel(D d) {
        if (d == null) return;
        mds.add(d);
        notifyDataSetChanged();
    }

    public void deleteMDatas() {
        mds.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mds != null ? mds.size() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        D d = mds.get(position);
        onBindDataViewHolder(holder, position, d);
    }

    protected abstract void onBindDataViewHolder(VH holder, int position, D d);
}
