package com.example.jaewanlee.mapmemo.KeywordSearchView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Core.MainApplication;
import com.example.jaewanlee.mapmemo.Map.KeywordSearchInterface;
import com.example.jaewanlee.mapmemo.Map.KeywordSearchRepo;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Constant;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.google.gson.GsonBuilder;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeywordSearchActivity extends AppCompatActivity {

    ImageButton back_img_btn;
    EditText search_edit_text;
    KeywordSearchInterface keywordSearchInterface;
    MainApplication mainApplication;

    RecyclerView searchResult_recyclerView;
    SearchResultAdapter recyclerView_adpaer;
    RecyclerView.LayoutManager recyclerView_manager;

    ArrayList keywordSearchDocumentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_search);

        back_img_btn = (ImageButton) findViewById(R.id.keyword_search_back_btn);
        search_edit_text = (EditText) findViewById(R.id.keyword_search_search_editText);
        searchResult_recyclerView = (RecyclerView) findViewById(R.id.keyword_search_recyclerView);
        //어플리케이션 데이터가져오기
        mainApplication = (MainApplication) getApplication();
        //키워드 검색을 위한 인터페이스 생성
        keywordSearchInterface = mainApplication.getKeywordSearchInterface();
        searchResult_recyclerView.setHasFixedSize(true);
        recyclerView_manager = new LinearLayoutManager(this);
        searchResult_recyclerView.setLayoutManager(recyclerView_manager);
        recyclerView_adpaer = new SearchResultAdapter(KeywordSearchActivity.this);
        searchResult_recyclerView.setAdapter(recyclerView_adpaer);
        searchResult_recyclerView.addItemDecoration(new RecyclerViewDecoration(25));

        final GestureDetector gestureDetector = new GestureDetector(KeywordSearchActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });


        searchResult_recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = searchResult_recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = searchResult_recyclerView.getChildViewHolder(child).getAdapterPosition();
                    Intent intent = new Intent();
                    if (keywordSearchDocumentList != null) {
                        if (keywordSearchDocumentList.get(position) != null) {
                            intent.putExtra("searchResult", new GsonBuilder().serializeNulls().create().toJson(keywordSearchDocumentList.get(position)));
                        }
                    }
                    setResult(Constant.SEARCH_QUERY_INTENT,intent);
                    finish();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        //전달받은 쿼리로 검색결과 보여주기
        Intent getintent = getIntent();
        setSearchResult(getintent.getStringExtra("search_query"));


        //검색버튼을 추가로 누를 경우
        search_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                setSearchResult(textView.getText().toString());
                return true;
            }
        });

        back_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //처음 데이터가 날라온 경우
    public void setSearchResult(String query) {
        Call<KeywordSearchRepo> call = keywordSearchInterface.getKeywordSearchRepo(query);

        call.enqueue(new Callback<KeywordSearchRepo>() {
            @Override
            public void onResponse(Call<KeywordSearchRepo> call, Response<KeywordSearchRepo> response) {
                if (response.isSuccessful() && response.body().getKeywordDocuments().size() > 0) {
                    Logger.d(response.body().getKeywordDocuments().get(0).getPlace_name());
                    keywordSearchDocumentList = new ArrayList(response.body().getKeywordDocuments());
                    recyclerView_adpaer.replaceDataSet(keywordSearchDocumentList);
                    recyclerView_adpaer.notifyDataSetChanged();
                } else {
                    //TODO 키워드가 아니라 도로명 주소나 일반 주소로검색하면 우째됨?
                    Toast.makeText(KeywordSearchActivity.this, "검색결과가 없습니다 다른 키워드로 검색해 주세요", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KeywordSearchRepo> call, Throwable t) {
                Toast.makeText(KeywordSearchActivity.this, "에러가 발생하였습니다 잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


}
