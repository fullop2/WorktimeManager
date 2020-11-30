package com.android.worktimemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.worktimemanager.DB.Schedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ScheduleAdder extends AppCompatActivity {

    TextView title;
    EditText time,comment;
    DatePicker date;
    RadioGroup types;
    Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_schedule_adder);

        initView();
        initListener();
    }

    private void initView()
    {
        btnAdd = (Button)findViewById(R.id.btnAdd);
        date = (DatePicker) findViewById(R.id.datePickerSchedule);
        time = (EditText)findViewById(R.id.textTime);
        title = (TextView)findViewById(R.id.textTitle);
        comment = (EditText)findViewById(R.id.textComment);
        types = (RadioGroup)findViewById(R.id.radioType);

        Intent intent = getIntent();
        LocalDate today = (LocalDate) intent.getSerializableExtra("today");
        date.updateDate(today.getYear(),today.getMonthValue()-1,today.getDayOfMonth());
    }

    private void initListener()
    {
        types.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(i == R.id.radioExtra || i == R.id.radioOver) {
                    comment.setVisibility(View.GONE);
                }
                else if(i == R.id.radioDayoff)
                {
                    comment.setVisibility(View.VISIBLE);
                }
            }
        });

        btnAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocalDate newDate = LocalDate.of(date.getYear(),date.getMonth()+1,date.getDayOfMonth());
                int type = types.getCheckedRadioButtonId();

                if(type == -1 || time.getText().toString().equals(""))
                {
                    Toast.makeText(ScheduleAdder.this,"시간, 타입을 설정해주십시오", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent();
                    String rComment = (String)((TextView)findViewById(type)).getText();
                    if(rComment.equals("연차") && !comment.getText().toString().equals(""))
                        rComment = comment.getText().toString();
                    intent.putExtra("Schedule", new Schedule(newDate.toString(), Double.parseDouble(time.getText().toString()), rComment));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
