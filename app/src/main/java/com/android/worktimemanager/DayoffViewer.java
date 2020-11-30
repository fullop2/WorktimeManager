package com.android.worktimemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.worktimemanager.Class.ListItemRemoveClickListener;
import com.android.worktimemanager.DB.DBHelper;
import com.android.worktimemanager.DB.Schedule;

import java.time.LocalDate;
import java.util.List;

public class DayoffViewer extends AppCompatActivity {

    DBHelper dbHelper = new DBHelper(this,"WORKDB",null);
    List listDayoff;
    ListView listViewDayoff;
    LocalDate curDate;
    ArrayAdapter arrayAdapterDayoff;
    TableLayout topStateView;
    Double doubleDayoff;
    Button btnEditTotalDayoff;
    TextView textLeftDayoff,textUsedDayoff,textTotalDayoff,textYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayoff_viewer);
        initState();
        updateDayoff();
    }

    @Override
    protected void onStop() {

        super.onStop();
        Intent intent = new Intent();
        intent.putExtra("day",doubleDayoff);
        finish();
    }

    private class ListDayoffRemoveClickListener extends ListItemRemoveClickListener
    {

        public ListDayoffRemoveClickListener(Context context, String tableName, List list) {
            super(context, tableName, list);
        }

        @Override
        protected void updateState()
        {
            updateDayoff();
        }

        @Override
        protected List getList() {
            return dbHelper.getSchedule(curDate,"DAYOFF",list,null);
        }

        @Override
        protected void removeItem() {
            dbHelper.remove((Schedule)list.get(selPosition));
        }
    }

    private void updateDayoff()
    {
        doubleDayoff = dbHelper.getTotalDayoff(curDate.getYear());
        listDayoff = dbHelper.getSchedule(curDate, "DAYOFF",listDayoff,null);
        arrayAdapterDayoff.notifyDataSetChanged();
        double offSum = 0;
        for(Object off : listDayoff)
        {
            offSum += ((Schedule)off).getTime();
        }

        textTotalDayoff.setText(Double.parseDouble(String.format("%.1f",doubleDayoff))+"일");
        textUsedDayoff.setText(Double.parseDouble(String.format("%.1f",offSum))+"일");
        textLeftDayoff.setText(Double.parseDouble(String.format("%.1f",doubleDayoff - offSum))+"일");
        textYear.setText(curDate.getYear()+"년");
    }
    private void initState()
    {
        Intent intent = getIntent();
        curDate = (LocalDate)intent.getSerializableExtra("today");
        textYear = (TextView)findViewById(R.id.textYear);
        textTotalDayoff = (TextView)findViewById(R.id.textTotalDayoff);
        textUsedDayoff = (TextView)findViewById(R.id.textTotalUsedDayoff);
        textLeftDayoff = (TextView)findViewById(R.id.textLeftDayoff);
        listViewDayoff = (ListView)findViewById(R.id.listDayoff);
        topStateView = (TableLayout)findViewById(R.id.layoutDayoffState);


        listDayoff = dbHelper.getSchedule(curDate, "DAYOFF",listDayoff,null);
        arrayAdapterDayoff = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listDayoff);
        listViewDayoff.setAdapter(arrayAdapterDayoff);
        listViewDayoff.setOnItemClickListener(new ListDayoffRemoveClickListener(DayoffViewer.this,"DAYOFF",listDayoff));
        topStateView.setOnClickListener(new YearPickerDialogClickListener());
        btnEditTotalDayoff = (Button)findViewById(R.id.btnEditTotalDayoff);
        btnEditTotalDayoff.setOnClickListener(new DialogDayoffEditorListener());
    }

    private class DialogDayoffEditorListener implements View.OnClickListener
    {
        EditText text;
        AlertDialog.Builder alert;
        @Override
        public void onClick(View view) {

            alert = new AlertDialog.Builder(DayoffViewer.this);
            text = new EditText(DayoffViewer.this);
            if(doubleDayoff != 0)
                text.setText(Double.toString(doubleDayoff));
            alert.setTitle(curDate.getYear()+"연도 연차 설정").setView(text).setNegativeButton("취소",null);

            alert.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dbHelper.setTotalDayoff(curDate.getYear(),Double.parseDouble(text.getText().toString()));
                    updateDayoff();
                }
            });
            alert.show();
        }
    }

    private class YearPickerDialogClickListener implements View.OnClickListener
    {
        AlertDialog.Builder alert;
        NumberPicker numberPicker;

        @Override
        public void onClick(View view) {
            alert = new AlertDialog.Builder(DayoffViewer.this);
            numberPicker = new NumberPicker(DayoffViewer.this);
            alert.setView(numberPicker);
            alert.setTitle("연도 설정");
            numberPicker.setMaxValue(9999);
            numberPicker.setMinValue(1);
            numberPicker.setValue(curDate.getYear());

            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    curDate = LocalDate.of(numberPicker.getValue(),1,1);
                    updateDayoff();
                }
            });
            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alert.show();
        }
    }
}