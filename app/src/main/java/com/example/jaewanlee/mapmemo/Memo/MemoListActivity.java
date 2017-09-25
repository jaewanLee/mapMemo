package com.example.jaewanlee.mapmemo.Memo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Shared.NetworkManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MemoListActivity extends AppCompatActivity {
    //상단 바
    ImageButton full_ib;
    ImageButton close_ib;
    //상단 탭
    LinearLayout tabBar_ll;
    ImageButton menu_ib;
    EditText search_et;
    ImageButton searchHistory_ib;
    Button share_bt;
    ImageButton search_ib;

    FrameLayout memolist_fl;

    RecyclerView recyclerView;

    LinearLayout sharelayout_LL;
    Button cancleSharing_bt;
    Button confirmSharing_bt;

    Realm realm;

    LinearLayoutManager layoutManager;
    ArrayList<MemoListDatabase> memoDatabases;
    MemoListAdapter memoListAdapter;
    MemoListShareAdapter memoListShareAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        full_ib = (ImageButton) findViewById(R.id.memoList_full_ImageButton);
        close_ib = (ImageButton) findViewById(R.id.memoList_close_ImageButton);

        tabBar_ll = (LinearLayout) findViewById(R.id.memoList_tabBar_LinearLayout);
        menu_ib = (ImageButton) findViewById(R.id.memoList_menu_ImageButton);
        search_et = (EditText) findViewById(R.id.memoList_searchView_editText);
        searchHistory_ib = (ImageButton) findViewById(R.id.memoList_searchHistory_ImageButton);
        share_bt = (Button) findViewById(R.id.memoList_share_Button);
        search_ib = (ImageButton) findViewById(R.id.memoList_search_ImageButton);

        recyclerView = (RecyclerView) findViewById(R.id.memoList_memos_recyclerView);

        memolist_fl = (FrameLayout) findViewById(R.id.memoList_list_FrameLayout);
        sharelayout_LL = (LinearLayout) findViewById(R.id.memoList_share_LinearLayout);
        cancleSharing_bt = (Button) findViewById(R.id.memoList_cancleSharing_Button);
        confirmSharing_bt = (Button) findViewById(R.id.memoList_confirmSharing_Button);

        realm = Realm.getDefaultInstance();

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        memoDatabases = new ArrayList<>();

        DatabaseInit(memoDatabases, memoListAdapter, recyclerView);

        share_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharelayout_LL.setVisibility(View.VISIBLE);
                memoListShareAdapter = new MemoListShareAdapter(getApplicationContext(), memoDatabases);
                recyclerView.setAdapter(memoListShareAdapter);
            }
        });

        confirmSharing_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MemoDatabase> requestValue=new ArrayList();
                //TODO 여기서 서버로 보내기
                for(MemoListDatabase memoListDatabase :memoListShareAdapter.memoDatabases){
                    if(memoListDatabase.isChecked){
                        requestValue.add(memoListDatabase.getMemoDatabase());
                        Toast.makeText(MemoListActivity.this, memoListDatabase.memoDatabase.getMemo_document_place_name(), Toast.LENGTH_SHORT).show();
                    }
                    memoListDatabase.setIsChecked(false);
                }

                NetworkManager networkManager=NetworkManager.getInstance();
                OkHttpClient client=networkManager.getClient();
                HttpUrl.Builder builder=new HttpUrl.Builder();

                builder.scheme("http");
                builder.host("115.71.236.6");
                builder.port(80);
                builder.addPathSegment("MapMemo");
                builder.addPathSegment("testRedis.php");

                String requestString=new Gson().toJson(requestValue);

                FormBody.Builder formBuilder=new FormBody.Builder().add("key","myValue").add("value",requestString);
                RequestBody body=formBuilder.build();

                final Request request=new Request.Builder()
                        .url(builder.build())
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MemoListActivity.this, "request Err", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                        final String result=response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MemoListActivity.this, result, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
//                retrofit = new Retrofit.Builder().baseUrl("http://localhost:8080/").build();
//                sharedMemoInterface = retrofit.create(SharedMemoInterface.class);
//                Call<String> request = sharedMemoInterface.getResult("testGID");
//                request.enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        Logger.d(response.body().toString());
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//
//                    }
//                });


                //다시 원상 복귀
                sharelayout_LL.setVisibility(View.INVISIBLE);
                memoListAdapter = new MemoListAdapter(getApplicationContext(), memoDatabases);
                recyclerView.setAdapter(memoListAdapter);

            }
        });

        cancleSharing_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharelayout_LL.setVisibility(View.INVISIBLE);
                memoListAdapter = new MemoListAdapter(getApplicationContext(), memoDatabases);
                recyclerView.setAdapter(memoListAdapter);
            }
        });

    }

    public void DatabaseInit(ArrayList<MemoListDatabase> memoDatabaseArrayList, MemoListAdapter memoListAdapter, RecyclerView recyclerView) {
        RealmResults<MemoDatabase> memoDatabaseRealmResults = realm.where(MemoDatabase.class).findAll();
        for (MemoDatabase memoDatabase : memoDatabaseRealmResults) {
            memoDatabaseArrayList.add(new MemoListDatabase(memoDatabase));
        }

        memoListAdapter = new MemoListAdapter(this, memoDatabases);
        recyclerView.setAdapter(memoListAdapter);

    }

}
