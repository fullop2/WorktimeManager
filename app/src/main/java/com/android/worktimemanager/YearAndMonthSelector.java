package com.android.worktimemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.time.LocalDate;

public class YearAndMonthSelector extends AppCompatActivity {

    NumberPicker yearPicker, monthPicker;
    Button btnOK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_and_month_selector);
        initView();
    }

    void initView()
    {
        yearPicker = (NumberPicker)findViewById(R.id.yearPicker);
        monthPicker = (NumberPicker)findViewById(R.id.monthPicker);
        btnOK = (Button)findViewById(R.id.buttonOK);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2999);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        Intent intent = getIntent();
        LocalDate curDate = (LocalDate)intent.getSerializableExtra("today");
        yearPicker.setValue(curDate.getYear());
        monthPicker.setValue(curDate.getMonthValue());

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate newDate = LocalDate.of(yearPicker.getValue(),monthPicker.getValue(),15);
                Intent intent = new Intent();
                intent.putExtra("newDate",newDate);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
