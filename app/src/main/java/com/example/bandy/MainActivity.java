package com.example.bandy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {

    private static final String CHANNEL_ID = "channel";
    private static final String CHANNEL_NAME = "Channel";
    private static final String ROOT_DIR = "/data/data/com.example.bandy/databases/";
    private final int[] check = {1, 2, 4, 8, 16, 32, 64};

    private TextView tvDate, tvTime;
    private ImageView ivMenu, ivWeather;
    private String weatherText;
    private String nx = "91";
    private String ny = "77";
    private double latitude;
    private double longitude;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NoticeAdapter adapter;

    LocationManager manager;
    Intent resultIntent;

    private GpsConverter gpsConverter;
    myDBHelper dbHelper;
    SQLiteDatabase sqlDB;
    private CheckTask checkTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoticeAdapter();
        recyclerView.setAdapter(adapter);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivWeather = (ImageView) findViewById(R.id.iv_weather);

        gpsConverter = new GpsConverter();
        dbHelper = new myDBHelper(this);

        try {
            boolean bResult = isCheckDB(this); // DB가 있는지?
            Log.d("Bandy DB : ", "DB Check="+bResult);
            if(!bResult){ // DB가 없으면 복사
                setDB(this);
            }else{ }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setRecyclerView();

        // Clock start
        ClockTask clockTask = new ClockTask();
        clockTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Weather start
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // GPS start
        startLocationService();

        adapter.setOnItemClickListener(new NoticeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                // 실행 내용
                Intent intent = new Intent(v.getContext(), SettingActivity.class);
                intent.putExtra("MODE", false);
                intent.putExtra("notiId", adapter.items.get(pos).getNotiId());
                resultLauncher.launch(intent);
            }
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.putExtra("MODE", true);
                resultLauncher.launch(intent);
            }
        });

        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    // DB 파일 존재하는지 체크
    private boolean isCheckDB(Context mContext) {
        String filePath = ROOT_DIR + "bandy";
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }

        return false;
    }

    // DB 파일 없을 시 assets 에서 복사 method
    private void setDB(Context mContext) {
        File folder = new File(ROOT_DIR);
        if(folder.exists()) {
        } else {
            folder.mkdirs();
        }
        AssetManager assetManager = mContext.getResources().getAssets();
        // db파일 이름 적어주기
        File outfile = new File(ROOT_DIR+"bandy");
        InputStream is = null;
        FileOutputStream fo = null;
        long filesize = 0;
        try {
            is = assetManager.open("bandy", AssetManager.ACCESS_BUFFER);
            filesize = is.available();
            if (outfile.length() <= 0) {
                byte[] tempdata = new byte[(int) filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } else {}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRecyclerView() {
        sqlDB = dbHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM Notice;", null);

        adapter.clear();

        if (cursor.getCount() <= 0) {
            // todo
            // db 에 저장된 알림이 없음
            // 알림을 생성하라는 View 띄워주기
            Toast.makeText(getApplicationContext(), "알림을 생성해주세요!", Toast.LENGTH_LONG).show();
            cursor.close();
            return;
        } else {
            while (cursor.moveToNext()) {
                // int notiId, String notiName,
                // String nodeId, String nodeName,
                // int notiTime, String startAt, String endAt,
                // int days, boolean isOn
                Notice notice = new Notice(
                        cursor.getInt(0),           //notiId    알림 ID
                        cursor.getString(1),        //notiName  알림 이름
                        cursor.getString(2),        //nodeId    정류장 ID
                        cursor.getString(3),        //nodeName  정류장 이름
                        cursor.getInt(4),           //notiTime  몇분전
                        cursor.getString(5),        //startAt   시작시각
                        cursor.getString(6),        //endAt     끝시각
                        cursor.getInt(7),           //days      요일
                        (cursor.getInt(8) > 0)      //isOn      토글
                );
                adapter.addItem(notice);
            }
        }
        cursor.close();

        Cursor routeCursor = null;
        int cnt = adapter.getItemCount();
        for (int i = 0; i < cnt; i++) {
            Notice curItem = adapter.getItem(i);
            routeCursor = sqlDB.rawQuery("SELECT routeID, routeName FROM RouteInNotice WHERE notiId=" + curItem.getNotiId() + ";", null);

            String[] routeIds = new String[2];
            String[] routeNames = new String[2];

            for (int j = 0; j < routeCursor.getCount(); j++) {
                routeCursor.moveToNext();
                routeIds[j] = routeCursor.getString(0);
                routeNames[j] = routeCursor.getString(1);
            }

            curItem.setRouteIds(routeIds);
            curItem.setRouteNames(routeNames);
        }

        recyclerView.setAdapter(adapter);
        routeCursor.close();
        sqlDB.close();

        checkTask = new CheckTask();
        checkTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private ActivityResultLauncher<Intent> resultLauncher  =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("MAIN ACTIVITY RETURN : ", " 확인");
                    resultIntent = result.getData();
                    setRecyclerView();
                }});

    // SQLite Open Class
    class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "bandy", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createNoticeQuery = "CREATE TABLE IF NOT EXISTS Notice(notiId integer primary key, notiName text, nodeId text,notiTime int ,startAt text, endAt text, days integer,  isOn integer(1));";
            String createRouteInNoticeQuery = "CREATE TABLE IF NOT EXISTS RouteInNotice(id primary key, notiId integer, routeID text, routeName text, foreign key(notiId) references Notice(notiId));";
            db.execSQL(createNoticeQuery);
            db.execSQL(createRouteInNoticeQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Notice");
            db.execSQL("DROP TABLE IF EXISTS RouteInNotice");
            onCreate(db);
        }
    }

    // Check Task Class
    class CheckTask extends AsyncTask<Void, Integer, Void> {

        private SimpleDateFormat timeFormat;
        private String endPoint = "http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList";
        private String key = "3%2BHvqTwlG7O1zyi19KPD86dGtKOEgQP8E%2FS5F9AKUCSD40mEjWhrHhO%2B56%2BlEtGBxrIVyF0ZoeEsImlx%2FbPWbQ%3D%3D";
        private String cityCode = "38010";
        private String nodeId;
        private String routeId;

        private StringBuilder urlBuilder;
        private XmlPullParserFactory xmlPullParserFactory;
        private XmlPullParser parser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            timeFormat = new SimpleDateFormat("hh:mm");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (!isCancelled()) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    int nWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    int cnt = adapter.getItemCount();
                    Thread.sleep(10000); // 10초 sleep

                    // 1. 전체 item 순회
                    for(int noticeNo = 0; noticeNo < cnt; noticeNo++) {
                        Notice item = adapter.getItem(noticeNo);
                        // 현재 시간과 설정 시간대 사이 체크
                        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String time = timeFormat.format(System.currentTimeMillis());
                        Date now = timeFormat.parse(time);
                        Date startTime = timeFormat.parse(item.getStartAt());
                        Date endTime = timeFormat.parse(item.getEndAt());

                        int days = item.getDays();
                        boolean[] set = new boolean[7];
                        for (int i = 0; i < 7; i++) {
                            set[i] = false;
                        }

                        for (int i = 0; i < 7; i++) {
                            if ((check[i] & days) == 1) {
                                set[i] = true;
                            }
                        }

                        boolean setDay = false;

                        if (nWeek == 1) {
                            if (set[6]) {
                                setDay = true;
                            }
                        } else if (nWeek == 2) {
                            if (set[0]) {
                                setDay = true;
                            }
                        } else if (nWeek == 3) {
                            if (set[1]) {
                                setDay = true;
                            }
                        } else if (nWeek == 4) {
                            if (set[2]) {
                                setDay = true;
                            }
                        } else if (nWeek == 5) {
                            if (set[3]) {
                                setDay = true;
                            }
                        } else if (nWeek == 6) {
                            if (set[4]) {
                                setDay = true;
                            }
                        } else if (nWeek == 7) {
                            if (set[5]) {
                                setDay = true;
                            }
                        }

                        if (now.after(startTime) && now.before(endTime) && setDay && item.isOn()) {
                            Log.e("CHECK TIME if : ", "IN");
                            nodeId = item.getNodeId();
                            String[] routes = item.getRouteIds();
                            for (int routeNo = 0; routeNo < 2; routeNo++) {
                                routeId = routes[routeNo];
                                if (!routeId.equals("")) {
                                    int arrTime = 0;
                                    urlBuilder = new StringBuilder(endPoint);
                                    urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key);
                                    urlBuilder.append("&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + cityCode);
                                    urlBuilder.append("&" + URLEncoder.encode("nodeId", "UTF-8") + "=" + nodeId);
                                    urlBuilder.append("&" + URLEncoder.encode("routeId", "UTF-8") + "=" + routeId);


                                    URL url = new URL(urlBuilder.toString());
                                    xmlPullParserFactory = XmlPullParserFactory.newInstance();
                                    parser = xmlPullParserFactory.newPullParser();

                                    InputStream is = url.openStream();
                                    parser.setInput(new InputStreamReader(is, "UTF-8"));
                                    String tagName = "";
                                    int eventType = parser.getEventType();

                                    while (eventType != XmlPullParser.END_DOCUMENT) {
                                        switch (eventType) {
                                            //태그가 시작
                                            case XmlPullParser.START_TAG:
                                                tagName=parser.getName();
                                                if (parser.getName().equals("item")) {
                                                    //객체 생성
                                                }
                                                break;
                                            //태그의 끝
                                            case XmlPullParser.END_TAG:
                                                if (parser.getName().equals("item")) {
                                                    //객체를 리스트에 추가
                                                }
                                                break;
                                            //태그 안의 텍스트
                                            case XmlPullParser.TEXT:
                                                switch(tagName) {
                                                    case "arrprevstationcnt":
                                                        break;
                                                    case "arrtime": {
                                                        arrTime = Integer.parseInt(parser.getText());
                                                        break;
                                                    }
                                                    case "nodeid":
                                                    case "nodenm":
                                                    case "routeid":
                                                    case "routeno":
                                                    case "routetp":
                                                    case "vehicletp":
                                                        break;
                                                }
                                                break;
                                        }
                                        //다음으로 이동
                                        eventType = parser.next();
                                    }

                                    publishProgress(noticeNo, routeNo, arrTime);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // todo
            super.onProgressUpdate(values);
            int noticeNo = values[0];
            int routeNo = values[1];
            int time = values[2];

            Notice notice = adapter.getItem(noticeNo);
            String arrTime = Integer.toString(time / 60);
            notice.setArrTimes(routeNo, arrTime + "분전");
            //recyclerView.notify();
            recyclerView.setAdapter(adapter);

            if (!notice.isFlag() && time <= notice.getNotiTime() * 60) {
                String msg = "[" + notice.getNodeName() +  "]" + notice.getRouteName(routeNo) + "번 버스가 " + arrTime + "분 후 도착합니다.";
                Notification(notice.getNotiName(), msg);
                notice.setFlag(true);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    // 시계 Task Class
    class ClockTask extends AsyncTask<Void, Void, Void> {
        SimpleDateFormat dateFormat;
        SimpleDateFormat timeFormat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dateFormat = new SimpleDateFormat("MM-dd\nEEE");
            timeFormat = new SimpleDateFormat("a hh:mm");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!isCancelled()) {
                publishProgress();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Date date = Calendar.getInstance().getTime();
            Date time = Calendar.getInstance().getTime();

            tvDate.setText(dateFormat.format(date));
            tvTime.setText(timeFormat.format(time));
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    // 날씨 Task Class
    class WeatherTask extends AsyncTask<Void, Void, String> {

        private String key = "3%2BHvqTwlG7O1zyi19KPD86dGtKOEgQP8E%2FS5F9AKUCSD40mEjWhrHhO%2B56%2BlEtGBxrIVyF0ZoeEsImlx%2FbPWbQ%3D%3D";
        private String endPoint = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        private String pageNo = "1";
        private String numOfRows = "500";
        private String dataType = "XML";
        private String base_date;
        private String base_time;
        private StringBuilder urlBuilder;

        private XmlPullParserFactory xmlPullParserFactory;
        private XmlPullParser parser;
        int sky, skyCount, pty, ptyCount;
        boolean snowFlag = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            sky = 0;
            skyCount = 0;
            pty = 0;
            ptyCount = 0;
            while (!isCancelled()) {

                Log.d("WDoInBackGround : ", "START");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = Calendar.getInstance().getTime();
                base_date = dateFormat.format(date);
                base_time = "0500";

                boolean skyFlag = false, ptyFlag = false;

                try {
                    urlBuilder = new StringBuilder(endPoint);
                    urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key);
                    urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + pageNo);
                    urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + numOfRows);
                    urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + dataType);
                    urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + base_date);
                    urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + base_time);
                    urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + nx);
                    urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + ny);

                    URL url = new URL(urlBuilder.toString());
                    xmlPullParserFactory = XmlPullParserFactory.newInstance();
                    parser = xmlPullParserFactory.newPullParser();

                    InputStream is = url.openStream();
                    parser.setInput(new InputStreamReader(is, "UTF-8"));
                    String tagName = "";
                    int eventType = parser.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            //태그가 시작
                            case XmlPullParser.START_TAG:
                                tagName=parser.getName();
                                if (parser.getName().equals("item")) {
                                    //객체 생성
                                }
                                break;
                            //태그의 끝
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equals("item")) {
                                    //객체를 리스트에 추가
                                }
                                break;
                            //태그 안의 텍스트
                            case XmlPullParser.TEXT:
                                switch(tagName) {
                                    case "baseDate":
                                    case "baseTime":
                                        break;
                                    case "category":{
                                        if (parser.getText().equals("SKY")) {
                                            skyFlag = true;
                                        } else if (parser.getText().equals("PTY")) {
                                            ptyFlag = true;
                                        }
                                        break;
                                    }
                                    case "fcstDate":
                                    case "fcstTime":
                                        break;
                                    case "fcstValue":{
                                        if (skyFlag == true) {
                                            if (skyCount < 12) {
                                                skyCount++;
                                                sky += Integer.parseInt(parser.getText());
                                            }
                                            skyFlag = false;
                                        } else if (ptyFlag == true) {
                                            if (ptyCount < 12) {
                                                if (parser.getText().equals("3")){
                                                    snowFlag = true;
                                                }
                                                ptyCount++;
                                                pty += Integer.parseInt(parser.getText());
                                            }
                                            ptyFlag = false;
                                        }
                                        break;
                                    }
                                    case "nx":
                                    case "ny":
                                        break;
                                }
                                break;
                        }
                        //다음으로 이동
                        eventType = parser.next();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                publishProgress();

                // sleep
                try {
                    Log.d("WEATHER SLEEP : ", "START");
                    Thread.sleep(3600000);
                    Log.d("WEATHER SLEEP : ", "END");
                } catch (InterruptedException e) {
                    Log.d("WBackground Exception:", e.getMessage());
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (skyCount > 0 && ptyCount > 0) {
                double resSky = (double) sky / (double) skyCount;
                double resPty = (double) pty / (double) ptyCount;
                if (snowFlag) {
                    weatherText = " [예상 날씨 : 눈]";
                    ivWeather.setImageResource(R.drawable.snowflakes);
                } else {
                    if (0 < resPty) {
                        weatherText = " [예상 날씨 : 비]";
                        ivWeather.setImageResource(R.drawable.rain);
                    }else {
                        if (0 <= resSky && resSky < 3) {
                            weatherText = " [예상 날씨 : 맑음]";
                            ivWeather.setImageResource(R.drawable.sunny);
                        } else {
                            weatherText = " [예상 날씨 : 흐림]";
                            ivWeather.setImageResource(R.drawable.cloudy);
                        }
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    // GPS 리스너 클래스
    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            String[] res = gpsConverter.start(latitude, longitude);
            nx = res[0];
            ny = res[1];
        }
    }

    // Location Manage Method
    private void startLocationService() {
        // Location 객체 참조하기
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Noti Method
    private void Notification(String title, String msg) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ));
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String mTitle = title + weatherText;
        builder.setContentTitle(mTitle);
        builder.setContentText(msg);
        builder.setSmallIcon(android.R.drawable.ic_menu_view);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Notification noti = builder.build();
        manager.notify(1, noti);
    }

    // Auto permissions check
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {
        // Toast.makeText(this, "permissions denied : " + strings.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int i, String[] strings) {
        // Toast.makeText(this, "permissions granted : " + strings.length, Toast.LENGTH_SHORT).show();
    }
}