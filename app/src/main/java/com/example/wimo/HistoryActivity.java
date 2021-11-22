package com.example.wimo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.wimo.Fragment.MapFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 화면 개요
 * EditText로 주소를 자동완성으로 입력받아서, "검색"버튼을 눌렀을때,
 * MapFragment를 불러옵니다.
 *
 * Fragment란, 화면안에 다른 화면을 불러오는건데, 지금은 Map을 그리는 화면을 History 화면에 불러옵니다.
 * 그 이유는 Context 관리때문인데, 자세한 내용은 생략하겠습니다.. !
 */

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "seo";

    private PrivacyInfoDB privacyInfoDB;
    private Button btn_search;
    private AutoCompleteTextView autoCompleteTextView;

    // Fragment (다른 화면 불러오는) 를 위한 변수
    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // SQLite DB Load
        privacyInfoDB = new PrivacyInfoDB(this);

        // 자동완성 컴포넌트 아이디할당
        autoCompleteTextView = findViewById(R.id.edt_input);

        // Fragment 관련 객체들 초기화
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        // 검색 버튼 아이디할당
        btn_search = findViewById(R.id.btn_search);

        // 검색버튼을 클릭했을때 이벤트
        btn_search.setOnClickListener(v -> {


            /**
             * 검색버튼을 클릭했을때 MapFragment를 불러옵니다.
             * 그냥 불러오는것이 아니라, autoCompleteTextView의 입력값을 전달하면서 불러옵니다.
             *
             * MapFragment에 대한 설명은 Fragment/MapFragment.java에 작성하겠습니다.
             */
            Bundle bundle = new Bundle();
            bundle.putString("address", autoCompleteTextView.getText().toString());

            replaceFragment(new MapFragment(), bundle); // 화면을 불러오는 함수 호출
        });

        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,  getRows() ));

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

            autoCompleteTextView.setText(parent.getItemAtPosition(position).toString());
        });
    }

    /**
     * DB에 있는 값을 ArrayList로 받아오는건 작성되있던거라 이해 하실거라 생각합니다.
     * 이 함수는, PrivacyInfo 단위로 담겨있는 List를, 주소만 빼서 새 List로 만들어 반환하는 함수입니다.
     *
     * 한마디로 ArrayList<PrivacyInfo> -> List<String> 으로 바꿔줍니다.  위치정보리스트 -> 주소문장 리스트
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> getRows(){

        return privacyInfoDB.loadInfoDB().stream().map(
                PrivacyInfo::getLocation
        ).collect(Collectors.toList());
    }

    // Fragment 화면 전환함수
    public static void replaceFragment(Fragment fragment, Bundle bundle){
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_root,fragment); // -> fragment_root라는 레이아웃을 MapFragment로 대체하라는뜻
        fragment.setArguments(bundle);
        fragmentTransaction.commit();   // 화면전환 시작!
        fragmentTransaction = fragmentManager.beginTransaction();
    }
}