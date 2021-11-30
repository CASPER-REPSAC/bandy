package com.example.bandy;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeSelector extends AppCompatActivity implements OnMapReadyCallback{

    String whereSelected = "창원시";
    String startBusList;
    //final static int changwonCode = 38010;
    private final String key = "-";
    private List<String> list;
    private ListView listView;
    private EditText editSearch;
    private SearchAdapter searchAdapter;
    private ArrayList<String> arrayList;
    private int eventType;
    Resources res;
    String[] localNodes_Name;
    String[] localNodes_ID;
    String[] localNodes_TP;
    String[] localNodes_GPS_LONG;
    String[] localNodes_GPS_LATI;

    //구글
    private GoogleMap mMap;
    CameraUpdateFactory cam;
    LatLng marker;

    private String selectedName = "현재 위치";
    private String selectedID;
    private String selectedGPS_Long = "128.695744";
    private String selectedGPS_Lati = "35.241426";
    Map<String, Integer> localList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Window Blur
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
        layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount= 0.0f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_node_selector);

        //Window Title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //WindowSize 팝업창 크기 조절
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.9); // Display 사이즈의 90%
        int height = (int) (dm.heightPixels * 0.6);  // Display 사이즈의 90%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        // 맵 나눠서 보여주기
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //Map Literal for local - local number
        Map<String, Integer> localList = new HashMap<String, Integer>();
        //localList.put("서울특별시"                  ,2         );
        localList.put("세종특별시"                  ,12         );
        localList.put("대구광역시"                  ,22         );
        localList.put("인천광역시"                  ,23         );
        localList.put("광주광역시"                  ,24         );
        localList.put("대전광역시/계룡시"             ,25         );
        localList.put("울산광역시"                  ,26         );
        localList.put("제주도"                     ,39         );
        //localList.put("부산광역시"                  ,51          );
        localList.put("수원시"                  ,31010        );
        localList.put("성남시"                  ,31020        );
        localList.put("의정부시"                 ,31030        );
        localList.put("안양시"                  ,31040        );
        localList.put("부천시"                  ,31050        );
        localList.put("광명시"                  ,31060        );
        localList.put("평택시"                  ,31070        );
        localList.put("동두천시"                 ,31080        );
        localList.put("안산시"                  ,31090        );
        localList.put("고양시"                  ,31100        );
        localList.put("과천시"                  ,31110        );
        localList.put("구리시"                  ,31120        );
        localList.put("남양주시"                 ,31130        );
        localList.put("오산시"                  ,31140        );
        localList.put("시흥시"                  ,31150        );
        localList.put("군포시"                  ,31160        );
        localList.put("의왕시"                  ,31170        );
        localList.put("하남시"                  ,31180        );
        localList.put("용인시"                  ,31190        );
        localList.put("파주시"                  ,31200        );
        localList.put("이천시"                  ,31210        );
        localList.put("안성시"                  ,31220        );
        localList.put("김포시"                  ,31230        );
        localList.put("화성시"                  ,31240        );
        localList.put("광주시"                  ,31250        );
        localList.put("양주시"                  ,31260        );
        localList.put("포천시"                  ,31270        );
        localList.put("여주군"                  ,31320        );
        localList.put("연천군"                  ,31350        );
        localList.put("가평군"                  ,31370        );
        localList.put("양평군"                  ,31380        );
        localList.put("춘천시"                  ,32010        );
        localList.put("원주시/횡성군"             ,32020        );
        localList.put("동해시"                  ,32040        );
        localList.put("태백시"                  ,32050        );
        localList.put("속초시"                  ,32060        );
        localList.put("삼척시"                  ,32070        );
        localList.put("홍천군"                  ,32310        );
        localList.put("영월군"                  ,32330        );
        localList.put("평창군"                  ,32340        );
        localList.put("정선군"                  ,32350        );
        localList.put("철원군"                  ,32360        );
        localList.put("화천군"                  ,32370        );
        localList.put("양구군"                  ,32380        );
        localList.put("인제군"                  ,32390        );
        localList.put("고성군"                  ,32400        );
        localList.put("양양군"                  ,32410        );
        localList.put("청주시"                  ,33010        );
        localList.put("충주시"                  ,33020        );
        localList.put("제천시"                  ,33030        );
        localList.put("보은군"                  ,33320        );
        localList.put("옥천군"                  ,33330        );
        localList.put("영동군"                  ,33340        );
        localList.put("진천군"                  ,33350        );
        localList.put("괴산군"                  ,33360        );
        localList.put("음성군"                  ,33370        );
        localList.put("단양군"                  ,33380        );
        localList.put("천안시"                  ,34010        );
        localList.put("공주시"                  ,34020        );
        localList.put("아산시"                  ,34040        );
        localList.put("서산시"                  ,34050        );
        localList.put("논산시"                  ,34060        );
        localList.put("부여군"                  ,34330        );
        localList.put("당진시"                  ,34390        );
        localList.put("전주시"                  ,35010        );
        localList.put("군산시"                  ,35020        );
        localList.put("익산시"                  ,35030        );
        localList.put("정읍시"                  ,35040        );
        localList.put("남원시"                  ,35050        );
        localList.put("김제시"                  ,35060        );
        localList.put("진안군"                  ,35320        );
        localList.put("무주군"                  ,35330        );
        localList.put("장수군"                  ,35340        );
        localList.put("임실군"                  ,35350        );
        localList.put("순창군"                  ,35360        );
        localList.put("고창군"                  ,35370        );
        localList.put("부안군"                  ,35380        );
        localList.put("목포시"                  ,36010        );
        localList.put("여수시"                  ,36020        );
        localList.put("순천시"                  ,36030        );
        localList.put("광양시"                  ,36060        );
        localList.put("구례군"                  ,36330        );
        localList.put("고흥군"                  ,36350        );
        localList.put("해남군"                  ,36400        );
        localList.put("영암군"                  ,36410        );
        localList.put("무안군"                  ,36420        );
        localList.put("함평군"                  ,36430        );
        localList.put("영광군"                  ,36440        );
        localList.put("신안군"                  ,36480        );
        localList.put("포항시"                  ,37010        );
        localList.put("경주시"                  ,37020        );
        localList.put("김천시"                  ,37030        );
        localList.put("구미시"                  ,37050        );
        localList.put("영주시"                  ,37060        );
        localList.put("영천시"                  ,37070        );
        localList.put("상주시"                  ,37080        );
        localList.put("문경시"                  ,37090        );
        localList.put("경산시"                  ,37100        );
        localList.put("군위군"                  ,37310        );
        localList.put("의성군"                  ,37320        );
        localList.put("청송군"                  ,37330        );
        localList.put("영덕군"                  ,37350        );
        localList.put("청도군"                  ,37360        );
        localList.put("고령군"                  ,37370        );
        localList.put("칠곡군"                  ,37390        );
        localList.put("봉화군"                  ,37410        );
        localList.put("울릉군"                  ,37430        );
        localList.put("창원시"                  ,38010        );
        localList.put("진주시"                  ,38030        );
        localList.put("통영시"                  ,38050        );
        localList.put("김해시"                  ,38070        );
        localList.put("밀양시"                  ,38080        );
        localList.put("거제시"                  ,38090        );
        localList.put("양산시"                  ,38100        );
        localList.put("의령군"                  ,38310        );
        localList.put("함안군"                  ,38320        );
        localList.put("창녕군"                  ,38330        );
        localList.put("고성군"                  ,38340        );
        localList.put("남해군"                  ,38350        );
        localList.put("하동군"                  ,38360        );
        localList.put("산청군"                  ,38370        );
        localList.put("함양군"                  ,38380        );
        localList.put("거창군"                  ,38390        );
        localList.put("합천군"                  ,38400        );

        //버스 노선 지역 리스트.
        //strings.xml이나 arrays.xml로 옮기기
        //서울, 부산은 다른 API를 통해 받아오기.
        String[] locals = {"창원시"};

        //Dropdown menu to choose local
        Spinner localSpinner = findViewById(R.id.localSpinner);
        ArrayAdapter<String> sidoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locals);
        sidoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        localSpinner.setAdapter(sidoAdapter);


        //set local and show where is selected
        localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                // set local id
                whereSelected = locals[position];
                //Toast.makeText(RouteSelector.this, whereSelected+"이(가) 선택되었습니다.",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0){
            }
        });

        //지역 버스 정류장 목록 가져오기.
        res = getResources();

        localNodes_Name = res.getStringArray(R.array.NODE_NAME_38010);
        localNodes_ID = res.getStringArray(R.array.NODE_ID_38010);
        localNodes_GPS_LONG = res.getStringArray(R.array.GPS_LONG_38010);
        localNodes_GPS_LATI = res.getStringArray(R.array.GPS_LATI_38010);
        localNodes_TP = res.getStringArray(R.array.NODE_TP_38010);

        //검색 기능 - 지역 정류장 목록 삽입
        arrayList = new ArrayList<String>();

        //Array -> List를 asList 변환 시, 아래와 같이로 선언해야 수정가능.
        list = new ArrayList<>(Arrays.asList(localNodes_Name));
        arrayList.addAll(list);

        //정류장 리스트 어댑터 연결
        searchAdapter = new SearchAdapter(list, this);
        //listView.setAdapter(searchAdapter);

        //자동 완성 검색
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        // AutoCompleteTextView에 어댑터를 연결.
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,  list ));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedName = localNodes_Name[list.indexOf(((TextView) view).getText().toString())];
                selectedID = localNodes_ID[list.indexOf(((TextView) view).getText().toString())];
                selectedGPS_Long = localNodes_GPS_LONG[list.indexOf(((TextView) view).getText().toString())];
                selectedGPS_Lati = localNodes_GPS_LATI[list.indexOf(((TextView) view).getText().toString())];
                Toast.makeText(getApplicationContext(), selectedID, Toast.LENGTH_SHORT).show();

                marker = new LatLng(Double.valueOf(selectedGPS_Lati), Double.valueOf(selectedGPS_Long));
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(marker)
                        .title(selectedName));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(16.0f);
        mMap.setMaxZoomPreference(25.0f);
        // Add a marker in Sydney and move the camera
        marker = new LatLng(Double.valueOf(selectedGPS_Lati), Double.valueOf(selectedGPS_Long));
        mMap.clear();

        mMap.addMarker(new MarkerOptions()
                .position(marker)
                .title(selectedName));
        cam.zoomTo(16.0f);
        mMap.moveCamera(cam.newLatLng(marker));
    }

    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arrayList);
        }
        // 문자 입력을 할때
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arrayList.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arrayList.get(i).toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(arrayList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어댑터터를 갱신하여 검색된 데이터를 화면에 보여준다.
        searchAdapter.notifyDataSetChanged();
    }

    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NODE_ID", selectedID);
        intent.putExtra("NODE_NAME", selectedName);
        setResult(RESULT_OK, intent);
        finish();
    }

    //구글맵 마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    };
}