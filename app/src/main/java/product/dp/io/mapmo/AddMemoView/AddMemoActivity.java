package product.dp.io.mapmo.AddMemoView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.mapmo.Core.MainApplication;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.Map.KeywordSearchRepo;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Util.Constant;
import product.dp.io.mapmo.Util.Logger;
import product.dp.io.mapmo.Util.TranscHash;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemoActivity extends AppCompatActivity implements OnMapReadyCallback {


    //지도관련
    MapFragment mapFragment;
    Marker tempMarker;
    ImageView memoTitle_back;
    TextView back_ib;

    EditText memoTitle;
    TextView create_date_tv;
    TextView category_tv;
    TextView memoAddr;
    EditText memoContent;


    Realm realm;

    KeywordSearchRepo.KeywordDocuments keywordDocuments;
    MemoDatabase memoDatabase;

    int tag;
    Intent getIntent;
    int memo_no;

    String latLngSearchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        memoTitle_back = (ImageView) findViewById(R.id.add_memo_title_background);
        back_ib = (TextView) findViewById(R.id.add_memo_back);
        Drawable drawable = memoTitle_back.getDrawable();
        drawable.setAlpha(200);
        memoTitle = (EditText) findViewById(R.id.add_memo_title);
        create_date_tv = (TextView) findViewById(R.id.add_memo_create_date_textView);
        category_tv = (TextView) findViewById(R.id.add_memo_categpry);
        memoAddr = (TextView) findViewById(R.id.add_memo_addr);
        memoContent = (EditText) findViewById(R.id.add_memo_text);

        realm = Realm.getDefaultInstance();

        getIntent = getIntent();
        tag = getIntent.getIntExtra("Tag", -1);

        //넘겨받은 인테트를 바탕으로 액티비티 내용 초기화 작업
        init();
        initCustomMap();

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
            }
        });

    }

    private void initCustomMap() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.add_memo_map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng markerLocation = new LatLng(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x()));
        tempMarker = googleMap.addMarker(new MarkerOptions().position(markerLocation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 15));
    }

    private void init() {

        if (tag == 1 || tag == Constant.MARKER_TAG_NEW) {
            memoDatabase = new Gson().fromJson(getIntent.getStringExtra("keywordDocument"), MemoDatabase.class);
            memoTitle.setText(memoDatabase.getMemo_document_place_name());
            if (memoDatabase.getMemo_document_road_address_name().equals(""))
                memoAddr.setText(memoDatabase.getMemo_document_address_name());
            else memoAddr.setText(memoDatabase.getMemo_document_road_address_name());
            String category = memoDatabase.getMemo_document_category_group_code();
            category_tv.setText(TranscHash.rawToreFinedCategory(category));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            Date currentTime = new Date(System.currentTimeMillis());
            String dTime = formatter.format(currentTime);
            create_date_tv.setText(dTime);

        } else if (tag == Constant.MARKER_TAG_CUSTOM) {
            memoDatabase = new Gson().fromJson(getIntent.getStringExtra("keywordDocument"), MemoDatabase.class);
            memoTitle.setText(memoDatabase.getMemo_document_place_name());

            //여기서 위경도로 위치 바꿔서 저장하기
            final LatLngSearchInterface latLngSearchInterface = MainApplication.getMainApplicationContext().getLatLngSearchInterface();
            Call<LatLngSearchRepo> call = latLngSearchInterface.getLatLngSearchRepo(memoDatabase.getMemo_document_x(), memoDatabase.getMemo_document_y());
            latLngSearchResult = "";
            call.enqueue(new Callback<LatLngSearchRepo>() {

                @Override
                public void onResponse(Call<LatLngSearchRepo> call, Response<LatLngSearchRepo> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getLatLngDocuments() != null && response.body().getLatLngDocuments().size() > 0) {

                            LatLngSearchRepo.LatLngDocuments latLngDocuments = response.body().getLatLngDocuments().get(0);

                            if (latLngDocuments.latLngRoadAddress != null)
                                latLngSearchResult = latLngDocuments.latLngRoadAddress.road_address_name;
                            else if (latLngDocuments.latLngAddress != null)
                                latLngSearchResult = latLngDocuments.latLngAddress.address_name;
                            else
                                latLngSearchResult = "";
                            memoDatabase.setMemo_document_road_address_name(latLngSearchResult);
                            memoAddr.setText(memoDatabase.getMemo_document_road_address_name());

                            memoDatabase.setMemo_document_place_url("http://map.daum.net/?map_type=DEFAULT&map_hybrid=false&q="+latLngSearchResult);
                        } else {
                            Logger.d("위경도 검색했으나 주소가 없음");
                            latLngSearchResult = "";
                            memoDatabase.setMemo_document_road_address_name(latLngSearchResult);
                            memoAddr.setText(memoDatabase.getMemo_document_road_address_name());
                            Toast.makeText(AddMemoActivity.this, "주소검색에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<LatLngSearchRepo> call, Throwable t) {
                    Logger.d(t.getMessage());
                    latLngSearchResult = "";
                    Toast.makeText(AddMemoActivity.this, "주소검색에 실패하였습니다", Toast.LENGTH_SHORT).show();
                    memoDatabase.setMemo_document_road_address_name(latLngSearchResult);
                    memoAddr.setText(memoDatabase.getMemo_document_road_address_name());
                }
            });


            String category = memoDatabase.getMemo_document_category_group_code();
            category_tv.setText(TranscHash.rawToreFinedCategory(category));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            Date currentTime = new Date(System.currentTimeMillis());
            String dTime = formatter.format(currentTime);
            create_date_tv.setText(dTime);
        } else {
            int memo_no = getIntent.getIntExtra("memo_no", -1);
            RealmResults<MemoDatabase> realmResults = realm.where(MemoDatabase.class).equalTo("memo_no", memo_no).findAll();
            if (realmResults.size() > 0) {
                memoDatabase = realmResults.first();
                memoTitle.setText(memoDatabase.getMemo_document_place_name());
                if (memoDatabase.getMemo_document_road_address_name().equals(""))
                    memoAddr.setText(memoDatabase.getMemo_document_address_name());
                else memoAddr.setText(memoDatabase.getMemo_document_road_address_name());
                memoContent.setText(memoDatabase.getMemo_content());
                String category = memoDatabase.getMemo_document_category_group_code();
                category_tv.setText(TranscHash.rawToreFinedCategory(category));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
                Date currentTime = new Date(memoDatabase.getMemo_createDate());
                String dTime = formatter.format(currentTime);
                create_date_tv.setText(dTime);
            }

        }

    }

    public void save() {
        RealmResults<MemoDatabase> realmResults = realm.where(MemoDatabase.class).findAll();
        String user_id = MainApplication.getMainApplicationContext().getOnUserDatabase().getUser_email();
        if (realmResults.size() > 10 && user_id.equals("guest")) {
            Toast.makeText(AddMemoActivity.this, "현재 손님계정이십니다 추가 메모를 사용하기 위해서는 로그인을 해주세요", Toast.LENGTH_LONG).show();
        } else {
            realm = Realm.getDefaultInstance();
            if (tag == 1 || tag == Constant.MARKER_TAG_NEW) {
                realm.beginTransaction();
                int lastData = -1;
//                    MemoDatabase lastDatbase = realm.createObject(MemoDatabase.class, number.intValue() + 1);
                if (realmResults.max("memo_no") != null)
                    lastData = realmResults.max("memo_no").intValue();
                memoDatabase.setMemo_no(lastData + 1);

                memoDatabase.setMemo_own_user_id(MainApplication.getMainApplicationContext().getOnUserDatabase().getUser_email());
                memoDatabase.setMemo_createDate(System.currentTimeMillis());

//                    memoDatabase.setDataFromKeyworDocuemnt(keywordDocuments);
                memoDatabase.setMemo_document_place_name(memoTitle.getText().toString());
                memoDatabase.setMemo_content(memoContent.getText().toString());
                memo_no = memoDatabase.getMemo_no();

                memoDatabase = realm.copyToRealm(memoDatabase);

                realm.commitTransaction();

                Intent intent = new Intent();
                intent.putExtra("addMemoResult", memo_no);
                setResult(Constant.ADD_MEMO_INTENT, intent);
                Logger.d("정상적으로 저장되었습니다");

            } else if (tag == Constant.MARKER_TAG_CUSTOM) {
                realm.beginTransaction();
                int lastData = -1;
//                    MemoDatabase lastDatbase = realm.createObject(MemoDatabase.class, number.intValue() + 1);
                if (realmResults.max("memo_no") != null)
                    lastData = realmResults.max("memo_no").intValue();
                memoDatabase.setMemo_no(lastData + 1);

                memoDatabase.setMemo_own_user_id(MainApplication.getMainApplicationContext().getOnUserDatabase().getUser_email());
                memoDatabase.setMemo_createDate(System.currentTimeMillis());

//                    memoDatabase.setDataFromKeyworDocuemnt(keywordDocuments);
                memoDatabase.setMemo_document_place_name(memoTitle.getText().toString());
                memoDatabase.setMemo_content(memoContent.getText().toString());
                memo_no = memoDatabase.getMemo_no();

                memoDatabase = realm.copyToRealm(memoDatabase);

                realm.commitTransaction();

                Intent intent = new Intent();
                intent.putExtra("addMemoResult", memo_no);
                setResult(Constant.ADD_MEMO_INTENT, intent);
                Logger.d("정상적으로 저장되었습니다");
            } else {
                realm.beginTransaction();
                memoDatabase = realm.where(MemoDatabase.class).equalTo("memo_no", getIntent.getIntExtra("memo_no", 0)).findFirst();
                memoDatabase.setMemo_document_place_name(memoTitle.getText().toString());
                memoDatabase.setMemo_content(memoContent.getText().toString());
                memo_no = memoDatabase.getMemo_no();
                Intent intent = new Intent();
                intent.putExtra("addMemoResult", memo_no);
                setResult(Constant.ADD_MEMO_INTENT, intent);
                Logger.d("정상적으로 저장되었습니다");
                realm.commitTransaction();
            }
            Toast.makeText(this, "자동저장 되었습니다", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    protected void onPause() {
        Logger.d("Pause");
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        if (!realm.isClosed() && !realm.isInTransaction())
            realm.close();
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {
        save();
        if (!realm.isInTransaction())
            Logger.d("transaction이 종료되었습니다");
        super.onBackPressed();
    }
}
