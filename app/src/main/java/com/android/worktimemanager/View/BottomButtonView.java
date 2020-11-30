package com.android.worktimemanager.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.worktimemanager.R;

public class BottomButtonView extends LinearLayout {

    private LinearLayout mLayout;
    private Button mBtn[];
    public BottomButtonView(Context context) {
        super(context);
        mBtn = new Button[3];
        initView();
    }

    public BottomButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBtn = new Button[4];
        initView();
    }

    private void initView()
    {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
        View v = li.inflate(R.layout.view_bottom_button,this,false);
        addView(v);

        mLayout = (LinearLayout)findViewById(R.id.layoutMonthData);
        mBtn[0] = (Button)findViewById(R.id.btnManageDayoff);
        mBtn[1] = (Button)findViewById(R.id.btnManageExtrawork);
        mBtn[2] = (Button)findViewById(R.id.btnManageHoliday);
        mBtn[3] = (Button)findViewById(R.id.btnUndo);
    }
    public Button[] getButton() {return mBtn;}


}
