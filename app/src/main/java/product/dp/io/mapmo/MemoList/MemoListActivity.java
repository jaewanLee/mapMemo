package product.dp.io.mapmo.MemoList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import product.dp.io.mapmo.Core.MainApplication;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.HomeView.HomeActivity;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Shared.NetworkManager;
import product.dp.io.mapmo.Util.Logger;

public class MemoListActivity extends AppCompatActivity {

    //상단 탭
    LinearLayout default_ll;
    ImageButton back_ib;
    ImageButton share_ib;

    LinearLayout shareTop_ll;
    ImageButton close_ib;

    RecyclerView recyclerView;

    LinearLayout sharelayout_LL;
    LinearLayout confirmSharing_ll;

    Realm realm;

    LinearLayoutManager layoutManager;
    ArrayList<MemoListDatabase> memoDatabases;
    MemoListAdapter memoListAdapter;
    MemoListShareAdapter memoListShareAdapter;

    String shared_memo_key;

    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(product.dp.io.mapmo.R.layout.activity_memo_list);

        Map<String,Object> eventValue=new HashMap<String,Object>();
        eventValue.put(AFInAppEventParameterName.CONTENT_LIST,"View List");
        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.CONTENT_VIEW,eventValue);

        default_ll = (LinearLayout) findViewById(R.id.memoList_default_LinearLayout);
        back_ib = (ImageButton) findViewById(R.id.memoList_menu_ImageButtona);
        share_ib = (ImageButton) findViewById(R.id.memoList_share_ImageButton);

        shareTop_ll = (LinearLayout) findViewById(R.id.memoList_share_linearLayout);
        close_ib = (ImageButton) findViewById(R.id.memoList_close_imageButton);

        recyclerView = (RecyclerView) findViewById(product.dp.io.mapmo.R.id.memoList_memos_recyclerView);

        sharelayout_LL = (LinearLayout) findViewById(product.dp.io.mapmo.R.id.memoList_share_LinearLayout);
        confirmSharing_ll = (LinearLayout) findViewById(R.id.memoList_confirmSharing_Button);

        realm = Realm.getDefaultInstance();

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        memoDatabases = new ArrayList<>();
        memoListAdapter = new MemoListAdapter(this, this, memoDatabases);

        gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        DatabaseInit(memoDatabases, memoListAdapter, recyclerView);

        if (DeepLink.hadOpened(this)) {

            onNewIntent(getIntent());

        }
        share_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharelayout_LL.setVisibility(View.VISIBLE);
                shareTop_ll.setVisibility(View.VISIBLE);
                default_ll.setVisibility(View.INVISIBLE);
                memoListShareAdapter = new MemoListShareAdapter(getApplicationContext(), memoDatabases);
                recyclerView.setAdapter(memoListShareAdapter);
            }
        });

        confirmSharing_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_email=MainApplication.getMainApplicationInstance().getOnUserDatabase().getUser_email();
                shared_memo_key = MainApplication.getMainApplicationInstance().getOnUserDatabase().getUser_email() + System.currentTimeMillis();
                final ArrayList<MemoListDatabase> requestValue = new ArrayList<>();

                for (MemoListDatabase memoListDatabase : memoListShareAdapter.memoDatabases) {
                    if (memoListDatabase.isChecked) {
                        requestValue.add(memoListDatabase);
                        Logger.d("comfirmed place name : " + memoListDatabase.getMemo_document_place_name());
                    }
                    memoListDatabase.setIsChecked(false);
                }

                NetworkManager networkManager = NetworkManager.getInstance();
                OkHttpClient client = networkManager.getClient();
                HttpUrl.Builder builder = new HttpUrl.Builder();

                builder.scheme("http");
                builder.host("ec2-52-199-177-224.ap-northeast-1.compute.amazonaws.com");
                builder.port(80);
                builder.addPathSegment("mapmo");
                builder.addPathSegment("memo");
                builder.addPathSegment("share");
                builder.addPathSegment("post.php");

                String requestString = new Gson().toJson(requestValue);
                //TODO myValue에다가 내 아이디랑 시간 써서 넣기
                FormBody.Builder formBuilder = new FormBody.Builder()
                        .add("key", shared_memo_key)
                        .add("value", requestString);
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
                                Logger.d("shared memo client.newCall err");
                                Toast.makeText(MemoListActivity.this, "요청에 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show();

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
                                    Logger.d("response of uploading shared memo data to server : " + result);
                                } else {
                                    Toast.makeText(MemoListActivity.this, "메모 데이터가 정상적으로 공유되지 않았습니다 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                                    Logger.d("response of uploading shared memo data to server : 400");
                                }
                            }
                        });
                        //Firebase에 이벤트 전송
                        Bundle bundle=new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"On HomeActivity");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,user_email);
                        MainApplication.getMainApplicationInstance().getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SHARE,bundle);


                        //이러고 나서 카카오쪽으로 보내기
                        String kakao_link_title = "";
                        for (MemoListDatabase requestMemoList : requestValue) {
                            kakao_link_title = kakao_link_title + " # " + requestMemoList.getMemo_document_place_name();
                        }
                        sendDefaultFeedTemplate(shared_memo_key, kakao_link_title);



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
                default_ll.setVisibility(View.VISIBLE);
                shareTop_ll.setVisibility(View.INVISIBLE);
                memoListAdapter = new MemoListAdapter(MemoListActivity.this, getApplicationContext(), memoDatabases);
                recyclerView.setAdapter(memoListAdapter);

            }
        });

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        close_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharelayout_LL.setVisibility(View.INVISIBLE);
                default_ll.setVisibility(View.VISIBLE);
                shareTop_ll.setVisibility(View.INVISIBLE);
                memoListAdapter = new MemoListAdapter(MemoListActivity.this, getApplicationContext(), memoDatabases);
                recyclerView.setAdapter(memoListAdapter);
            }
        });
    }

    private void sendDefaultFeedTemplate(String memo_no, String memo_titles) {
        //TODO templet 이름 및 이미지 바꾸기
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("MapMo",
                        "http://ec2-52-199-177-224.ap-northeast-1.compute.amazonaws.com/mapmo/appstore_mapmo.png",
                        LinkObject.newBuilder()
                                .setAndroidExecutionParams("key=" + memo_no)
                                .build())
                        .setDescrption(memo_titles)
                        .build())
                .addButton(new ButtonObject("앱으로 보기", LinkObject.newBuilder()
                        .setAndroidExecutionParams("key=" + memo_no)
                        .build()))
                .build();


        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                com.kakao.util.helper.log.Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
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

    public ArrayList<MemoListDatabase> keywordSearch(String keyWord) {
        ArrayList<MemoListDatabase> memoDatabaseArrayList = new ArrayList();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MemoDatabase> addressSearchResults = realm
                .where(MemoDatabase.class)
                .contains("memo_document_road_address_name", keyWord)
                .or()
                .contains("memo_document_address_name", keyWord)
                .findAll();
        if (addressSearchResults.size() > 0) {
            for (MemoDatabase memoDatabase : addressSearchResults) {
                memoDatabaseArrayList.add(new MemoListDatabase(memoDatabase));
            }
        }
        return memoDatabaseArrayList;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String data = intent.getDataString();
        if(data!=null){
            int valuePosition = data.indexOf("key");
            String shared_memo_key = data.substring(valuePosition + 4);
            Logger.d("deeplink shared key : " + shared_memo_key);
            //여기다가 MemoDatabaseARrayList에다가 가져온 데이터 추가하고 adapter에다가 notify보내기
            NetworkManager networkManager = NetworkManager.getInstance();
            OkHttpClient client = networkManager.getClient();
            HttpUrl.Builder builder = new HttpUrl.Builder();


            builder.scheme("http");
            builder.host("ec2-52-199-177-224.ap-northeast-1.compute.amazonaws.com");
            builder.port(80);
            builder.addPathSegment("mapmo");
            builder.addPathSegment("memo");
            builder.addPathSegment("share");
            builder.addPathSegment("get.php");
            builder.addQueryParameter("key", shared_memo_key);

            Logger.d(builder.build().toString());

            final Request request = new Request.Builder()
                    .url(builder.build())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String result = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.d(result);
                            Gson gson = new Gson();
                            if (response.code() != 200 || !response.message().equals("OK")) {
                                Toast.makeText(MemoListActivity.this, "네트워크 상태가 불안정합니다 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            realm = Realm.getDefaultInstance();
                            try {
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
                                        memoDatabase.setMemo_no(lastData + 1);
                                        memoDatabase.setDataFromMemoListDatabase(shared_memoListDatabase);
                                        realm.beginTransaction();
                                        memoDatabase = realm.copyToRealm(memoDatabase);

                                        realm.commitTransaction();
                                        shared_memoListDatabase.setIsNew(true);
                                        memoDatabases.add(shared_memoListDatabase);
                                    }
                                }
                                memoListAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Toast.makeText(MemoListActivity.this, "메모 생성 시기가 만료되었습니다. 메모를 새로 전송해 주세요", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            AirBridge.getTracker().onNewIntent(intent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public void onBackPressed() {
        if (sharelayout_LL.getVisibility() == View.VISIBLE) {
            sharelayout_LL.setVisibility(View.INVISIBLE);
            default_ll.setVisibility(View.VISIBLE);
            shareTop_ll.setVisibility(View.INVISIBLE);
            memoListAdapter = new MemoListAdapter(MemoListActivity.this, getApplicationContext(), memoDatabases);
            recyclerView.setAdapter(memoListAdapter);
        } else if (getIntent() == null) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
