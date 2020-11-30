package com.android.worktimemanager.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.worktimemanager.R;

public class CurrentStateView extends LinearLayout {

    private TextView mTotalWorkTime;
    private TextView mUsedDayoff;
    private TextView mOvertime;
    private TextView mHolidayTime;
    private TextView mThisMonth;
    private TextView mLeftDayoff;
    public CurrentStateView(Context context)
    {
        super(context);
        initView();
    }

    public CurrentStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView()
    {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
        View v = li.inflate(R.layout.view_current_state,this,false);
        addView(v);

        mTotalWorkTime = (TextView)findViewById(R.id.textViewTotalWorkTime);
        mUsedDayoff = (TextView)findViewById(R.id.textViewUsedDayoff);
        mOvertime = (TextView)findViewById(R.id.textViewOvertime);
         mHolidayTime= (TextView)findViewById(R.id.textViewHolidayWork);
         mThisMonth = (TextView)findViewById(R.id.textViewMonth);
         mLeftDayoff= (TextView)findViewById(R.id.textViewLeftDayoff);

    }

    public TextView getTotalWorkTime() {
        return mTotalWorkTime;
    }
    public TextView getUsedDayoff() {
        return mUsedDayoff;
    }
    public TextView getOvertime() {
        return mOvertime;
    }
    public TextView getHolidayTime() {
        return mHolidayTime;
    }
    public TextView getThisMonth() {
        return mThisMonth;
    }
    public TextView getLeftDayoff() {
        return mLeftDayoff;
    }
}
