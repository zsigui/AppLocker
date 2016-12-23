package com.noahmob.AppLocker.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.Widget.SlideSwitch;
import com.noahmob.AppLocker.Widget.SlideSwitch.SlideListener;
import com.noahmob.AppLocker.entity.SettingItem;

import java.util.ArrayList;

public class SettingAdapter extends ArrayAdapter<SettingItem> {
    static final /* synthetic */ boolean $assertionsDisabled = (!SettingAdapter.class.desiredAssertionStatus());
    Activity activity;
    ArrayList<SettingItem> item_list;
    public CheboxChangeListener mCheboxChangeListener;
    int resource_id;

    public interface CheboxChangeListener {
        void changed(boolean z, int i);
    }

    public void setCheckBoxChangeLister(CheboxChangeListener listener) {
        this.mCheboxChangeListener = listener;
    }

    public SettingAdapter(Activity activity, int resource_id, ArrayList<SettingItem> browser_grid_data) {
        super(activity, resource_id, browser_grid_data);
        this.activity = activity;
        this.resource_id = resource_id;
        this.item_list = browser_grid_data;
    }

    public int getCount() {
        return this.item_list.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        VierHolder holder;
        int i;
        int i2 = 0;
        View myView = convertView;
        if (convertView == null) {
            holder = new VierHolder();
            myView = this.activity.getLayoutInflater().inflate(this.resource_id, parent, false);
            if ($assertionsDisabled || myView != null) {
                holder.app_text = (TextView) myView.findViewById(R.id.item_text);
                holder.arrow = (ImageView) myView.findViewById(R.id.arrow);
                holder.checkbox = (SlideSwitch) myView.findViewById(R.id.onoff);
                myView.setTag(holder);
            } else {
                throw new AssertionError();
            }
        }
        holder = (VierHolder) myView.getTag();
        holder.checkbox.setSlideListener(null);
        SettingItem myitem = (SettingItem) this.item_list.get(position);
        holder.app_text.setText(myitem.getTitle());
        ImageView imageView = holder.arrow;
        if (myitem.getType() == 1) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        SlideSwitch slideSwitch = holder.checkbox;
        if (myitem.getType() != 0) {
            i2 = 8;
        }
        slideSwitch.setVisibility(i2);
        setCheckState(position, holder);
        ChenckClickableState(position, myView, holder);
        return myView;
    }

    private void ChenckClickableState(int position, View myView, VierHolder holder) {
        switch (((SettingItem) getItem(position)).getAction()) {
            case 3:
                holder.app_text.setTextColor(AppLockerPreference.getInstance(getContext()).isRelockPoliceEnabled() ? ViewCompat.MEASURED_STATE_MASK : Color.rgb(170, 170, 170));
                return;
            default:
                return;
        }
    }

    private void setCheckState(final int positon, VierHolder holder) {
        switch (((SettingItem) getItem(positon)).getAction()) {
            case 0:
                holder.checkbox.setState(AppLockerPreference.getInstance(getContext()).isServiceEnabled());
                holder.checkbox.setSlideListener(new SlideListener() {
                    public void open() {
                        if (SettingAdapter.this.mCheboxChangeListener != null) {
                            SettingAdapter.this.mCheboxChangeListener.changed(true, positon);
                        }
                    }

                    public void close() {
                        if (SettingAdapter.this.mCheboxChangeListener != null) {
                            SettingAdapter.this.mCheboxChangeListener.changed(false, positon);
                        }
                    }
                });
                return;
            case 2:
                holder.checkbox.setState(AppLockerPreference.getInstance(getContext()).isRelockPoliceEnabled());
                holder.checkbox.setSlideListener(new SlideListener() {
                    public void open() {
                        if (SettingAdapter.this.mCheboxChangeListener != null) {
                            SettingAdapter.this.mCheboxChangeListener.changed(true, positon);
                        }
                    }

                    public void close() {
                        if (SettingAdapter.this.mCheboxChangeListener != null) {
                            SettingAdapter.this.mCheboxChangeListener.changed(false, positon);
                        }
                    }
                });
                return;
            case 3:
                int relockTime = AppLockerPreference.getInstance(getContext()).getRelockTimeout();
                String showStr = "current " + relockTime + " minutes";
                if (relockTime < 0) {
                    showStr = "current " + AppLockerPreference.getInstance(getContext()).getLastRelockTimeout() + " minutes";
                }
                holder.app_text.setText(showStr);
                return;
            default:
                return;
        }
    }
}
