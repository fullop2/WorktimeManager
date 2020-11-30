package com.android.worktimemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.worktimemanager.Class.ApiExplorer;
import com.android.worktimemanager.Class.ListItemRemoveClickListener;
import com.android.worktimemanager.Class.OnSwipeListener;
import com.android.worktimemanager.DB.DBHelper;
import com.android.worktimemanager.DB.Holiday;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class HolidayManagement extends AppCompatActivity {

    private DBHelper dbHelper = new DBHelper(this,"WORKDB",null);
    private TextView date;
    private ListView holidays;
    private ArrayAdapter adapter;
    private List holidayList;
    private LocalDate curDate;
    private Button buttonAddHoliday,buttonGetHolidayFromServer;
    private ApiExplorer apiExplorer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_management);
        initView();
        initListener();
        updateView();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Holiday holiday = new Holiday(((LocalDate)data.getSerializableExtra("newDate")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),(String)data.getSerializableExtra("newHoliday"));
            if(!dbHelper.add(holiday))
                Toast.makeText(this,"이미 존재합니다",Toast.LENGTH_SHORT);
        }
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            curDate = (LocalDate)data.getSerializableExtra("newDate");
        }
        updateView();
    }
    private void initView()
    {
        date = (TextView)findViewById(R.id.textYearMonth);
        holidays = (ListView)findViewById(R.id.listHoliday);
        buttonAddHoliday = (Button)findViewById(R.id.buttonAddHoliday);
        buttonGetHolidayFromServer = (Button)findViewById(R.id.buttonGetHolidayFromServer);
        holidayList = new ArrayList();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, holidayList);
        holidays.setAdapter(adapter);

        Intent intent = getIntent();
        curDate = (LocalDate)intent.getSerializableExtra("today");
    }
    private void initListener()
    {
        buttonAddHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HolidayManagement.this,HolidayAdder.class);
                startActivityForResult(intent,1);
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(HolidayManagement.this,YearAndMonthSelector.class);
               intent.putExtra("today",curDate);
               startActivityForResult(intent,2);
            }
        });
        holidays.setOnItemClickListener(new HolidayListClickListener(this,"HOLIDAY",holidayList) {
            @Override
            protected void updateState() {
                updateView();
            }
        });
        buttonGetHolidayFromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Boolean msgOn = false;
                    apiExplorer = new ApiExplorer();
                    List holidays = getHolidayList();
                    if(holidays==null)
                    {
                        Toast.makeText(HolidayManagement.this, "인터넷 연결을 확인하십시오", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        for (Object holiday : holidays) {
                            if (!dbHelper.add((Holiday) holiday))
                                msgOn = true;
                        }
                        if (msgOn)
                            Toast.makeText(HolidayManagement.this, "이미 휴일로 등록되어있습니다", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(HolidayManagement.this, "성공적으로 공휴일을 불러왔습니다", Toast.LENGTH_SHORT).show();
                    }

                }
                catch(Exception e)
                {
                    System.err.println(e.getMessage());
                }
                updateView();
            }
        });
        findViewById(R.id.holidayManagementLayout).setOnTouchListener(new HolidaySwipeListener());
    }
    private void updateView()
    {
        holidayList = dbHelper.getHoliday(curDate,holidayList);
        adapter.notifyDataSetChanged();
        date.setText(curDate.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))+" ~ "+curDate.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
    }
    private List getHolidayList()
    {
        List<Holiday> tmpHolidayList = new ArrayList();
        try {
            apiExplorer = new ApiExplorer();
            List xmls = apiExplorer.getData(curDate.getYear());
            if(xmls == null)
                return null;
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            InputSource inputSource = new InputSource();

            for(Object xml : xmls)
            {
                inputSource.setCharacterStream(new StringReader((String)xml));
                Document doc = dBuilder.parse(inputSource);
                NodeList list = doc.getElementsByTagName("item");
                for(int i = 0; i < list.getLength(); i++)
                {
                    Element tmp = (Element) list.item(i);

                    LocalDate date = LocalDate.parse(tmp.getChildNodes().item(3).getTextContent(),DateTimeFormatter.ofPattern("yyyyMMdd"));
                    String name = tmp.getChildNodes().item(1).getTextContent();
                    tmpHolidayList.add(new Holiday(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),name));
                }
            }
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

        return tmpHolidayList;
    }


    private class HolidayListClickListener extends ListItemRemoveClickListener
    {

        public HolidayListClickListener(Context context, String tableName, List list) {
            super(context, tableName, list);
        }

        @Override
        protected void updateState() {
            updateView();
        }

        @Override
        protected List getList() {
            return dbHelper.getHoliday(curDate,list);
        }

        @Override
        protected void removeItem() {
            dbHelper.remove((Holiday)list.get(selPosition));
        }
    }
    private class HolidaySwipeListener extends OnSwipeListener
    {
        @Override
        protected void updateState() {
            updateView();
        }

        @Override
        protected void leftSwipe() {
            curDate = curDate.minusMonths(1);
        }

        @Override
        protected void rightSwipe() {
            curDate = curDate.plusMonths(1);
        }
    }
}
