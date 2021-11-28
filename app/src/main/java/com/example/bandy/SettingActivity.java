package com.example.bandy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    final static int ADD_STARTPOINT = 1;
    final static int ADD_WAYPOINT = 2;
    final static int ADD_ENDPOINT = 3;
    private static final String LOG_TAG = "MainActivity2";
    private MapView mMapView;
    TextView txtResult;
    TextView nodeName;
    TextView nodeID;

    //for Title
    TextView titleView;
    String title;
    EditText inputTitle;

    //for days
    Integer days = 0;
    Integer daysCheck = 0;


    //for time
    String startAt;
    String endAt;
    Integer notiTime = 10;
    RadioGroup notiRadio;


    //for NodeSelect Intent
    Intent INTENTRESULT;
    String NODEID = null;
    String NODENAME = null;


    //for RouteSelect Intent
    Intent RouteIntentResult;
    ArrayList<String> RouteIdList = new ArrayList<String>();
    ArrayList<String> RouteNameList = new ArrayList<String>();


    //for create
    Button btnCreate;
    private myDBHelper alarmHelper = new myDBHelper(this);;
    SQLiteDatabase bandy;
    Cursor cursor;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    RecyclerView startPointRe;
    String bus[], station[];

    List busList = new ArrayList<>();
    List stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        //Title editText
        titleView = findViewById(R.id.routeTitle);
        inputTitle = (EditText) findViewById(R.id.routeTitle);
        inputTitle.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                switch (keyCode){
                    case KeyEvent.KEYCODE_ENTER:
                        titleView.setText(inputTitle.getText());
                        title = inputTitle.getText().toString();
                        Log.d("title",title);
                        break;
                }
                return true;
            }
        });


        //days Selector
        CheckBox mon = (CheckBox)findViewById(R.id.monSelector);
        CheckBox tues = (CheckBox) findViewById(R.id.tuesSelector);
        CheckBox wed = (CheckBox)findViewById(R.id.wednesSelector);
        CheckBox thur = (CheckBox) findViewById(R.id.thursSelector);
        CheckBox fri = (CheckBox)findViewById(R.id.friSelector);
        CheckBox sat = (CheckBox)findViewById(R.id.saturSelector);
        CheckBox sun = (CheckBox)findViewById(R.id.sunSelector);
        //monday : 64
        mon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                daysCheck = (days & 64) >> 6;
                if(daysCheck == 1){
                    days = days & (127 - 64);
                }else{
                    days = days | 64;
                }
                Log.d("days",Integer.toString(days));
            }
        });
        //tuesday : 32
        tues.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                daysCheck = (days & 32) >> 5;
                if(daysCheck == 1){
                    days = days & (127 - 32);
                }else{
                    days = days | 32;
                }
                Log.d("days",Integer.toString(days));
            }
        });
        //wednesday : 16
        wed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                daysCheck = (days & 16) >> 4;
                if(daysCheck == 1){
                    days = days & (127 - 16);
                }else{
                    days = days | 16;
                }
                Log.d("days",Integer.toString(days));
            }
        });
        //thursday : 8
        thur.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                daysCheck = (days & 8) >> 3;
                if(daysCheck == 1){
                    days = days & (127 - 8);
                }else{
                    days = days | 8;
                }
                Log.d("days",Integer.toString(days));
            }
        });
        //friday : 4
        fri.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                daysCheck = (days & 4) >> 2;
                if(daysCheck == 1){
                    days = days & (127 - 4);
                }else{
                    days = days | 4;
                }
                Log.d("days",Integer.toString(days));
            }
        });
        //saturday : 2
        sat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                daysCheck = (days & 2) >> 1;
                if(daysCheck == 1){
                    days = days & (127 - 2);
                }else{
                    days = days | 2;
                }
                Log.d("days",Integer.toString(days));
            }
        });
        //sunday : 1
        sun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                daysCheck = (days & 1);
                if(daysCheck == 1){
                    days = days & (127 - 1);
                }else{
                    days = days | 1;
                }
                Log.d("days",Integer.toString(days));
            }
        });

        //Time Setting Buttons
        Button btnSetStartTime = (Button) findViewById(R.id.btnSetStartTime);
        Button btnSetEndTime = (Button) findViewById(R.id.btnSetEndTime);
        TextView btnStartTime = findViewById(R.id.btnSetStartTime);
        TextView btnEndTime = findViewById(R.id.btnSetEndTime);

        //Start Point View
        startPointRe = findViewById(R.id.startPointView);
        //bus = getResources().getStringArray(R.array.bus_num);
        //station = getResources().getStringArray(R.array.station_name);

        //Collections.addAll(busList,bus);
        //Collections.addAll(stationList,station);


        //Time Setting listener
        btnSetStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog sTimePicker = new TimePickerDialog(
                        SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        btnStartTime.setText(String.format("%02d:%02d",hourOfDay,minute));
                        startAt =btnStartTime.getText().toString();
                    }
                }, 0, 0, true);
                sTimePicker.show();
            }
        });

        btnSetEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog eTimePicker = new TimePickerDialog(
                        SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        btnEndTime.setText(String.format("%02d:%02d",hourOfDay,minute));
                        endAt = btnEndTime.getText().toString();
                    }
                }, 0, 0, true);
                eTimePicker.show();
            }
        });


        //for notiTime
        notiRadio = findViewById(R.id.notiTimeGroup);
        notiRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.min10:
                        notiTime = 10;
                        break;
                    case R.id.min15:
                        notiTime = 15;
                        break;
                    case R.id.min20:
                        notiTime = 20;
                        break;
                }
            }
        });


        //for create
        btnCreate = (Button) findViewById(R.id.alarmCreate);

        btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(RouteIdList == null || title == null){
                    Log.d("Nodata","Input all data");
                }else{
                    if (days > 127){
                        days = 127;
                    }else if(days < 0){
                        days = 0;
                    }

                    bandy = alarmHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(alarmHelper.notiName,title);
                    values.put(alarmHelper.nodeId,NODEID);
                    values.put(alarmHelper.nodeName,NODENAME);
                    values.put(String.valueOf(alarmHelper.notiTime),notiTime);
                    values.put(alarmHelper.startAt,startAt);
                    values.put(alarmHelper.endAt,endAt);
                    values.put(alarmHelper.days,days);
                    values.put(alarmHelper.isOn,1);
                    bandy.insert(alarmHelper.Notice,null,values);

                    bandy = alarmHelper.getReadableDatabase();
                    cursor = bandy.rawQuery("Select notiId from Notice Order by notiId DESC limit 1;",null);
                    cursor.moveToFirst();
                    Integer notiId = cursor.getInt(0);
                    Log.d("notiId", notiId.toString());

                    bandy = alarmHelper.getWritableDatabase();

                    Log.d("title",title);
                    Log.d("days",days.toString());
                    Log.d("start",startAt);
                    Log.d("end",endAt);
                    Log.d("nodeid",NODEID);
                    Log.d("nodename",NODENAME);
                    for(int i = 0; i < RouteIdList.size() ;i++){
                        values.clear();
                        values.put(String.valueOf(alarmHelper.notiId),notiId);
                        values.put(alarmHelper.routeID,RouteIdList.get(i));
                        values.put(alarmHelper.routeName,RouteNameList.get(i));
                        bandy.insert(alarmHelper.RouteInNotice,null,values);
                        Log.d("Routeid",RouteIdList.get(i));
                        Log.d("Routename",RouteNameList.get(i));
                    }
                }
            }

        });
    }

    //타이틀 입력 후 엔터키 확인하기
    public boolean onKey(View v, int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            switch (v.getId()){
                case R.id.routeTitle:
                    title = inputTitle.toString();
                    Log.d("title",title);
                    break;
            }
            return true;
        }
        return false;
    }

    public void mOnPopupClick(View v){
        //데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(this, NodeSelector.class);
        resultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> resultLauncher  =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("Enter", " 확인");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d("Result Code Check", "결과 코드 확인");

                        INTENTRESULT = result.getData();
                        //데이터 받기
                        NODEID = INTENTRESULT.getStringExtra("NODE_ID");
                        NODENAME = INTENTRESULT.getStringExtra("NODE_NAME");

                        Log.d("DATA",NODENAME);
                        if (NODEID == null || result == null) {
                            Log.d("Result Node Error", "No data");
                        } else {
                            Log.d("Result Node Check", NODENAME);
                        }
                    }

                }});

    public void mOnBusClick(View v){
        if(NODEID == null){
            Toast.makeText(getApplicationContext(), "정류장을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, RouteSelector.class);
            intent.putExtra("nodeid",NODEID);
            busLauncher.launch(intent);
        }
    }
    private ActivityResultLauncher<Intent> busLauncher  =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("Enter", " 확인");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d("Result Code Check", "결과 코드 확인");
                        //선택한 버스 데이터 받기
                        RouteIntentResult = new Intent();
                        RouteIntentResult = result.getData();

                        RouteNameList = new ArrayList<String>();
                        RouteIdList = new ArrayList<String>();

                        RouteIdList = RouteIntentResult.getStringArrayListExtra("RouteId_List");
                        RouteNameList = RouteIntentResult.getStringArrayListExtra("RouteName_List");

                        for(int i = 0; i < RouteIdList.size() ;i++){
                            Log.d("Routeid",RouteIdList.get(i));
                            Log.d("Routename",RouteNameList.get(i));
                        }
                    }

                }});
    public void OnClick(){

    }
    public class myDBHelper extends SQLiteOpenHelper {
        public final static String Notice = "Notice";
        public final static String notiName = "notiName";
        public final static String nodeId = "nodeId";
        public final static String nodeName = "nodeName";
        public final static String notiTime = "notiTime";
        public final static String startAt = "startAt";
        public final static String endAt = "endAt";
        public final static String days = "days";
        public final static String isOn = "isOn";

        public final static String RouteInNotice = "RouteInNotice";
        public final static String notiId = "notiId";
        public final static String routeID = "routeID";
        public final static String routeName = "routeName";


        @Override
        public void onCreate(SQLiteDatabase db) {
            String createNoticeQuery = "CREATE TABLE IF NOT EXISTS Notice(notiId integer primary key, notiName text, nodeId text,notiTime int ,startAt text, endAt text, days integer,  isOn integer(1));";
            String createRouteInNoticeQuery = "CREATE TABLE IF NOT EXISTS RouteInNotice(id primary key, notiId integer, routeID text, routeName text, foreign key(notiId) references Notice(notiId));";
            db.execSQL(createNoticeQuery);
            db.execSQL(createRouteInNoticeQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        }

        public myDBHelper(Context context){
            super(context, "bandy",null,1);
        }
    }
}