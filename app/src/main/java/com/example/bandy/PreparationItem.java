package com.example.bandy;

public class PreparationItem {

    //체크박스로 선택되었는지 판단
    boolean checked;

    //버스 정보
    String ItemStringid;
    String ItemStringname;

    //생성자
    PreparationItem(boolean b, String i, String n){
        checked = b;
        ItemStringid = i;
        ItemStringname = n;
    }

    public boolean isChecked(){
        return checked;
    }
}
