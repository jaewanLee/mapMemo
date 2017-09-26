package com.example.jaewanlee.mapmemo.Memo;

import android.content.Intent;
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
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import io.airbridge.AirBridge;
import io.airbridge.deeplink.DeepLink;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        memoListAdapter = new MemoListAdapter(this, memoDatabases);

        DatabaseInit(memoDatabases, memoListAdapter, recyclerView);

        if (DeepLink.hadOpened(this)) {

            onNewIntent(getIntent());

        }
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
                ArrayList<MemoListDatabase> requestValue = new ArrayList<>();
                //TODO 여기서 서버로 보내기
                for (MemoListDatabase memoListDatabase : memoListShareAdapter.memoDatabases) {
                    if (memoListDatabase.isChecked) {
                        requestValue.add(memoListDatabase);
                        Toast.makeText(MemoListActivity.this, memoListDatabase.getMemo_document_place_name(), Toast.LENGTH_SHORT).show();
                    }
                    memoListDatabase.setIsChecked(false);
                }

                NetworkManager networkManager = NetworkManager.getInstance();
                OkHttpClient client = networkManager.getClient();
                HttpUrl.Builder builder = new HttpUrl.Builder();

                builder.scheme("http");
                builder.host("115.71.236.6");
                builder.port(80);
                builder.addPathSegment("MapMemo");
                builder.addPathSegment("shared_data_post.php");

                String requestString = new Gson().toJson(requestValue);
                //TODO myValue에다가 내 아이디랑 시간 써서 넣기
                FormBody.Builder formBuilder = new FormBody.Builder().add("key", "myValue").add("value", requestString);
                RequestBody body = formBuilder.build();

                final Request request = new Request.Builder()
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
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.contains("200")) {
                                    Toast.makeText(MemoListActivity.this, result, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MemoListActivity.this, "request Err 400", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

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
        recyclerView.setAdapter(memoListAdapter);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Toast.makeText(this, "deepLink Clicked", Toast.LENGTH_SHORT).show();
        //여기다가 MemoDatabaseARrayList에다가 가져온 데이터 추가하고 adapter에다가 notify보내기
        NetworkManager networkManager = NetworkManager.getInstance();
        OkHttpClient client = networkManager.getClient();
        HttpUrl.Builder builder = new HttpUrl.Builder();

        builder.scheme("http");
        builder.host("115.71.236.6");
        builder.port(80);
        builder.addPathSegment("MapMemo");
        builder.addPathSegment("shared_data_get.php");
        builder.addQueryParameter("key", "myValue");

        Logger.d(builder.build().toString());

        Request request = new Request.Builder()
                .url(builder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.d(result);
                        Gson gson = new Gson();

                        realm = Realm.getDefaultInstance();
                        ArrayList<MemoListDatabase> shared_memoListDatabases = (ArrayList<MemoListDatabase>) gson.fromJson(result,
                                new TypeToken<ArrayList<MemoListDatabase>>() {
                                }.getType());

                        for (int i = 0; i < shared_memoListDatabases.size(); i++) {

                            MemoListDatabase shared_memoListDatabase = shared_memoListDatabases.get(i);
                            long no = realm.where(MemoDatabase.class).equalTo("memo_document_x", shared_memoListDatabase.getMemo_document_x()).equalTo("memo_document_y", shared_memoListDatabase.getMemo_document_y()).count();

                            if (no <= 0) {
                                realm = Realm.getDefaultInstance();
                                int lastData = 0;
                                if (realm.where(MemoDatabase.class).findFirst() != null) {
                                    lastData = realm.where(MemoDatabase.class).max("memo_no").intValue();

                                }
                                MemoDatabase memoDatabase = new MemoDatabase();
                                memoDatabase.setMemo_no(lastData+1);
                                memoDatabase.setDataFromMemoListDatabase(shared_memoListDatabase);
                                realm.beginTransaction();
                                memoDatabase = realm.copyToRealm(memoDatabase);

                                realm.commitTransaction();
                                shared_memoListDatabase.setIsNew(true);
                                memoDatabases.add(shared_memoListDatabase);
                            }


                        }
                        memoListAdapter.notifyDataSetChanged();
                    }
                });


            }
        });

        AirBridge.getTracker().onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
