package com.android.worktimemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.time.LocalDate;

public class HolidayAdder extends AppCompatActivity {

    private LocalDate curDate;
    private DatePicker datePicker;
    private Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_adder);

        initView();
    }

    void initView()
    {
        curDate = LocalDate.now();
        datePicker = (DatePicker)findViewById(R.id.datePickerHoliday);
        btnAdd = (Button)findViewById(R.id.btnAdd);

        datePicker.updateDate(curDate.getYear(),curDate.getMonthValue()-1,curDate.getDayOfMonth());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate newDate = LocalDate.of(datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth());
                Intent intent = new Intent();
                intent.putExtra("newDate",newDate);
                intent.putExtra("newHoliday",((TextView)findViewById(R.id.textComment)).getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
