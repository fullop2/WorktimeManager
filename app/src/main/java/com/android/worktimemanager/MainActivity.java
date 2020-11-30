package com.android.worktimemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.worktimemanager.Class.ListItemRemoveClickListener;
import com.android.worktimemanager.Class.OnSwipeListener;
import com.android.worktimemanager.DB.*;
import com.android.worktimemanager.View.BottomButtonView;
import com.android.worktimemanager.View.CurrentStateView;
import com.android.worktimemanager.View.MonthDataView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper = new DBHelper(this,"WORKDB",null);

    private List listExtraWork, listOvertimeWork;
    private ArrayAdapter adapterExtraWork, adapterOvertimeWork;
    private LocalDate curDate;

;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        curDate = LocalDate.now().withDayOfMonth(1).plusDays(14);
        if(LocalDate.now().getDayOfMonth() <= 15)
            curDate = curDate.minusMonths(1);

        initMonthList();
        initButtonView();
        updateMainState();
        initState();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1 && resultCode == RESULT_OK)
        {
            Schedule s = (Schedule) data.getSerializableExtra("Schedule");
            if(!dbHelper.add(s))
            {
                updateMainState();
            }
        }
        else if(requestCode == 3 && resultCode == RESULT_OK)
        {
            curDate = (LocalDate)data.getSerializableExtra("newDate");
        }
        updateMainState();
    }

    // Implements Listeners
    private class ButtonScheduleAdderClickListener implements Button.OnClickListener
    {
        private int requestCode;

        ButtonScheduleAdderClickListener(int requestCode)
        {
            this.requestCode = requestCode;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,ScheduleAdder.class);
            intent.putExtra("today",LocalDate.now());
            startActivityForResult(intent,requestCode);
        }
    }
    private class MainSwipeListener extends OnSwipeListener
    {
        @Override
        protected void updateState()
        {
            updateMainState();
        }

        @Override
        protected void leftSwipe()
        {
            curDate = curDate.minusMonths(1);
        }

        @Override
        protected void rightSwipe() {
            curDate = curDate.plusMonths(1);
        }

    }
    private class ListWorkItemRemoveClickListener extends ListItemRemoveClickListener
    {

        public ListWorkItemRemoveClickListener(Context context,String tableName, List list) {
            super(context,tableName,list);
        }

        @Override
        protected void updateState()
        {
            updateMainState();
        }

        @Override
        protected List getList() {
            return dbHelper.getSchedule(curDate,tableName,list,null);
        }

        @Override
        protected void removeItem() {
            dbHelper.remove((Schedule)list.get(selPosition));
        }
    }
    // private Methods

    private void initButtonView()
    {
        Button btns[] = ((BottomButtonView)findViewById(R.id.bottomButton)).getButton();
        btns[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DayoffViewer.class);
                if(LocalDate.now().getMonthValue() != curDate.getMonthValue())
                    intent.putExtra("today", curDate);
                else
                    intent.putExtra("today", LocalDate.now());
                startActivityForResult(intent,2);
            }
        });
        btns[1].setOnClickListener(new ButtonScheduleAdderClickListener(1));
        btns[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HolidayManagement.class);
                intent.putExtra("today",curDate);
                startActivityForResult(intent,3);
            }
        });
        btns[3].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if(dbHelper.getLastTable() == null) {
                    Toast.makeText(MainActivity.this, "되돌릴 작업이 없습니다", Toast.LENGTH_LONG).show();
                    return;
                }
                final String lastOp = dbHelper.getLastOperator();
                final DAO lastObj = dbHelper.getLastDAO();
                String msgOp;
                if(lastOp.equals("INSERT"))
                    msgOp = "삽입";
                else
                    msgOp = "삭제";
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setMessage("마지막 작업을 되돌리시겠습니까?\n"+msgOp+" : "+lastObj.toString());
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.undoLastQuery();
                        Toast.makeText(MainActivity.this,"성공적으로 되돌렸습니다",Toast.LENGTH_SHORT);
                        updateMainState();
                    }
                });
                alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    private void initMonthList()
    {
        listOvertimeWork = dbHelper.getSchedule(curDate,"EXTRAWORK",listOvertimeWork,"연장");
        listExtraWork = dbHelper.getSchedule(curDate,"EXTRAWORK",listExtraWork,"특근");

        adapterOvertimeWork = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listOvertimeWork);
        adapterExtraWork = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listExtraWork);

        MonthDataView monthDataView = (MonthDataView)findViewById(R.id.currentMonthData);
        monthDataView.getOvertimework().setAdapter(adapterOvertimeWork);
        monthDataView.getExtrawork().setAdapter(adapterExtraWork);

        monthDataView.getOvertimework().setOnItemClickListener(new ListWorkItemRemoveClickListener(MainActivity.this,"EXTRAWORK",listOvertimeWork));
        monthDataView.getExtrawork().setOnItemClickListener(new ListWorkItemRemoveClickListener(MainActivity.this,"EXTRAWORK",listExtraWork));
    }
    private void initState()
    {

        CurrentStateView stateView = (CurrentStateView)findViewById(R.id.currentState);
        stateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YearAndMonthSelector.class);
                intent.putExtra("today",curDate);
                startActivityForResult(intent,3);
            }
        });
        findViewById(R.id.layoutMain).setOnTouchListener(new MainSwipeListener());

    }
    private void updateMainState()
    {

        double totalDayoff = dbHelper.getTotalDayoff(curDate.getYear());
        listOvertimeWork = dbHelper.getSchedule(curDate,"EXTRAWORK",listOvertimeWork,"연장");
        listExtraWork = dbHelper.getSchedule(curDate,"EXTRAWORK",listExtraWork,"특근");
        List listDayoff = dbHelper.getSchedule(curDate,"DAYOFF",null,null);
        List listHoliday = dbHelper.getHoliday(curDate,null);
        adapterOvertimeWork.notifyDataSetChanged();
        adapterExtraWork.notifyDataSetChanged();


        double overWorkSum = 0, holidayWorkSum = 0,offDaySum = 0;
        for(Object time : listExtraWork)
            holidayWorkSum += ((Schedule)time).getTime();
        for(Object time : listOvertimeWork)
            overWorkSum += ((Schedule)time).getTime();
        for(Object time : listDayoff)
            offDaySum += ((Schedule)time).getTime();

        double tmpWorkTime = 0.0;
        LocalDate endDate = curDate.plusMonths(1).plusDays(1);
        for(LocalDate tmpDate = curDate.plusDays(1); !tmpDate.isEqual(endDate); tmpDate = tmpDate.plusDays(1))
        {
            double dayWorkTime = 1.0;

            if(tmpDate.getDayOfWeek() == DayOfWeek.SATURDAY || tmpDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                dayWorkTime = dayWorkTime - 1.0;
            else
            {
                for(Object date : listDayoff) {
                    LocalDate offDay = LocalDate.parse(((Schedule)date).getDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (offDay.isEqual(tmpDate))
                    {
                        dayWorkTime = dayWorkTime - ((Schedule)date).getTime();
                        break;
                    }
                }
                for(Object date : listHoliday)
                {
                    LocalDate holidayDate = LocalDate.parse(((Holiday)date).getDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (holidayDate.isEqual(tmpDate))
                    {
                        dayWorkTime = dayWorkTime - 1.0;
                        break;
                    }
                }
            }
            if(dayWorkTime > 0.0)
                tmpWorkTime += dayWorkTime;
        }

        double totalWorkTime = tmpWorkTime*8 + overWorkSum + holidayWorkSum;
        CurrentStateView curState = (CurrentStateView)findViewById(R.id.currentState);
        curState.getOvertime().setText(Double.parseDouble(String.format("%.1f",overWorkSum))+"시간");
        curState.getHolidayTime().setText(Double.parseDouble(String.format("%.1f",holidayWorkSum))+"시간");
        curState.getUsedDayoff().setText(Double.parseDouble(String.format("%.1f",offDaySum))+"일");
        curState.getLeftDayoff().setText(Double.parseDouble(String.format("%.1f",totalDayoff - offDaySum))+"일");
        curState.getTotalWorkTime().setText(Double.parseDouble(String.format("%.1f",totalWorkTime))+"시간");
        curState.getThisMonth().setText(curDate.plusDays(1).format(DateTimeFormatter.ofPattern("MM/dd"))+ " ~ " + curDate.plusMonths(1).format(DateTimeFormatter.ofPattern("MM/dd")));
    }
}
