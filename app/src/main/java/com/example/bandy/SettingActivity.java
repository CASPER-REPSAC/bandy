package com.example.bandy;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.MapView;

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

    //alarmMode
    //true : create
    //false : modify
    boolean mode;
    int notiId;
    int isOn;


    //for Title
    String title;
    EditText inputTitle;

    //for days
    Integer days = 0;
    Integer daysCheck = 0;

    //for time
    TextView btnStartTime;
    TextView btnEndTime;
    String startAt;
    String endAt;
    Integer notiTime = 10;
    RadioGroup notiRadio;


    //for NodeSelect Intent
    TextView nodeView;
    Intent INTENTRESULT;
    String NODEID = null;
    String NODENAME = null;


    //for RouteSelect Intent
    Intent RouteIntentResult;
    ArrayList<String> RouteIdList = new ArrayList<String>();
    ArrayList<String> RouteNameList = new ArrayList<String>();
    TextView busView ;

    Button btnCreate;
    Button btnDelete;
    Button btnCancel;


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


        //textView 미리 세팅
        nodeView = (TextView) findViewById(R.id.startPointSelector);

        //radio 미리 세팅
        notiRadio = findViewById(R.id.notiTimeGroup);

        //Title editText
        inputTitle = (EditText) findViewById(R.id.routeTitle);
        inputTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                title = inputTitle.getText().toString();
                Log.d("title",title);
            }
        });

        //Time Setting Buttons
        Button btnSetStartTime = (Button) findViewById(R.id.btnSetStartTime);
        Button btnSetEndTime = (Button) findViewById(R.id.btnSetEndTime);
        btnStartTime = (TextView)findViewById(R.id.btnSetStartTime);
        btnEndTime = (TextView)findViewById(R.id.btnSetEndTime);

        //days Selector
        CheckBox mon = (CheckBox)findViewById(R.id.monSelector);
        CheckBox tues = (CheckBox) findViewById(R.id.tuesSelector);
        CheckBox wed = (CheckBox)findViewById(R.id.wednesSelector);
        CheckBox thur = (CheckBox) findViewById(R.id.thursSelector);
        CheckBox fri = (CheckBox)findViewById(R.id.friSelector);
        CheckBox sat = (CheckBox)findViewById(R.id.saturSelector);
        CheckBox sun = (CheckBox)findViewById(R.id.sunSelector);

        //bus TextView
        busView =(TextView)findViewById(R.id.busSelector);

        //button
        btnCreate = (Button) findViewById(R.id.alarmCreate);
        btnDelete = (Button)findViewById(R.id.alarmDelete);
        btnCancel = (Button) findViewById(R.id.alarmCancel);

        //for modify Check
        Intent modeIntent = getIntent();
        mode = modeIntent.getBooleanExtra("MODE",true);
        if (mode == false) {

            btnDelete.setEnabled(true);

            notiId = modeIntent.getIntExtra("notiId", -1);
            bandy = alarmHelper.getReadableDatabase();
            cursor = bandy.rawQuery("Select * from Notice where notiId=" + Integer.toString(notiId) + ";", null);
            cursor.moveToFirst();
            if (notiId != cursor.getInt(0)) {
                Toast.makeText(getApplicationContext(), "알람이 없습니다!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                title = cursor.getString(1); //notiname
                inputTitle.setText(title, TextView.BufferType.EDITABLE);

                NODEID = cursor.getString(2); //nodeid
                NODENAME = cursor.getString(3);//nodename
                nodeView.setText(NODENAME);
                notiTime = cursor.getInt(4);//notitime int
                startAt = cursor.getString(5);//startat
                endAt = cursor.getString(6);//endat
                btnStartTime.setText(startAt);
                btnEndTime.setText(endAt);
                days = cursor.getInt(7);//days int
                daysChecker(days, mode, mon, tues, wed, thur, fri, sat, sun);
                isOn = cursor.getInt(8);//is On int
                RouteIdList = null;
                RouteNameList = null;
            }
        }

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
        notiRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.min5:
                        notiTime = 5;
                        break;
                    case R.id.min10:
                        notiTime = 10;
                        break;
                    case R.id.min15:
                        notiTime = 15;
                        break;
                }
            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (RouteIdList.size() == 0 || RouteIdList == null || title == null || NODENAME == null || startAt == null || endAt == null || days == 0) {
                    Toast.makeText(getApplicationContext(), "모두 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    if(title.trim().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "모두 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        if (days > 127){
                            days = 127;
                        }else if(days < 0){
                            days = 127;
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
                        if(mode) {
                            bandy.insert(alarmHelper.Notice, null, values);
                            //bandy = alarmHelper.getReadableDatabase();
                            cursor = bandy.rawQuery("Select notiId from Notice Order by notiId DESC limit 1;",null);
                            cursor.moveToFirst();
                            notiId = cursor.getInt(0);
                            Log.d("notiId", String.valueOf(notiId));



                        }else{
                            bandy.update(alarmHelper.Notice,values,"notiId = ?",new String[] { Integer.toString(notiId)} ); //알람 업데이트
                            for(int i = 0; i < RouteIdList.size() ;i++) {
                                bandy.delete(alarmHelper.RouteInNotice,  "notiId = ?", new String[]{Integer.toString(notiId)}); // 알람 노섬 삭제
                            }
                        }


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
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }


            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandy = alarmHelper.getWritableDatabase();
                bandy.delete(alarmHelper.RouteInNotice,  "notiId = ?", new String[]{Integer.toString(notiId)}); // 알람 노섬 삭제
                bandy.delete(alarmHelper.Notice,  "notiId = ?", new String[]{Integer.toString(notiId)}); // 알람 노섬 삭제
                bandy.close();
                finish();
            }
        });
    }

    private void daysChecker(Integer days, boolean mode, CheckBox mon, CheckBox tues, CheckBox wed, CheckBox thur, CheckBox fri, CheckBox sat, CheckBox sun) {
        if(!mode){
            if ((days & 64) >> 6 == 1){
                mon.setChecked(true);
            }
            if ((days & 32) >> 5 == 1){
                tues.setChecked(true);
            }
            if ((days & 16) >> 4 == 1){
                wed.setChecked(true);
            }
            if ((days & 8) >> 3 == 1){
                thur.setChecked(true);
            }
            if ((days & 4) >> 2 == 1){
                fri.setChecked(true);
            }
            if ((days & 2) >> 1 == 1){
                sat.setChecked(true);
            }
            if ((days & 1) == 1){
                sun.setChecked(true);
            }
        }
    }


    /*
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
    }*/

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
                            nodeView = (TextView) findViewById(R.id.startPointSelector);
                            nodeView.setText(NODENAME);
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
                        String bView = "";
                        for(int i = 0; i < RouteIdList.size() ;i++){
                            Log.d("Routeid",RouteIdList.get(i));
                            Log.d("Routename",RouteNameList.get(i));

                            bView = bView + RouteNameList.get(i) + " ";
                        }

                        busView.setText(bView);
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

        public void onDelete(SQLiteDatabase db, int notiId){
            final String deleteNoticeQuery = "DELETE FROM Notice WHERE notiId =";
            db.execSQL(deleteNoticeQuery + Integer.toString(notiId));
        }

        public myDBHelper(Context context){
            super(context, "bandy",null,1);
        }
    }
}