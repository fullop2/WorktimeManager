package com.android.worktimemanager.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.worktimemanager.R;

public class MonthDataView extends LinearLayout {
    private LinearLayout mLayout;
    private ListView mListOvertimework;
    private ListView mListExtrawork;

    public MonthDataView(Context context) {
        super(context);
        initView();

    }
    public MonthDataView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        initView();
    }

    private void initView()
    {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
        View v = li.inflate(R.layout.view_month_data,this,false);
        addView(v);

        mLayout = (LinearLayout)findViewById(R.id.layoutMonthData);
        mListOvertimework = (ListView)findViewById(R.id.listOvertimeWork);
        mListExtrawork = (ListView)findViewById(R.id.listExtrawork);


    }

    public ListView getOvertimework()
    {
        return mListOvertimework;
    }
    public ListView getExtrawork()
    {
        return mListExtrawork;
    }


}
