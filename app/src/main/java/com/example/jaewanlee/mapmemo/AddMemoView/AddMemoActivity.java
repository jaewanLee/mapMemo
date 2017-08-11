package com.example.jaewanlee.mapmemo.AddMemoView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.Map.KeywordSearchRepo;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Constant;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.example.jaewanlee.mapmemo.Util.TranscHash;
import com.google.gson.Gson;

import io.realm.Realm;

import static com.example.jaewanlee.mapmemo.Util.Constant.ADD_MEMO_INTENT;

public class AddMemoActivity extends AppCompatActivity {

    EditText memoTitle;
    TextView memoAddr;
    Spinner memoCategory;
    EditText memoContent;
    ImageButton saveButton;

    Realm realm;

    KeywordSearchRepo.KeywordDocuments keywordDocuments;

    int tag;
    Intent getIntent;
    int memo_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        memoTitle = (EditText) findViewById(R.id.add_memo_title);
        memoAddr = (TextView) findViewById(R.id.add_memo_addr);
        memoCategory = (Spinner) findViewById(R.id.add_memo_category);
        memoContent = (EditText) findViewById(R.id.add_memo_text);
        saveButton = (ImageButton) findViewById(R.id.add_memo_save_imageButton);

        ArrayAdapter categoryAdapter = ArrayAdapter.createFromResource(this, R.array.spinnerArrayCategory, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memoCategory.setAdapter(categoryAdapter);

        realm = Realm.getDefaultInstance();

        getIntent = getIntent();
        tag = getIntent.getIntExtra("Tag", -1);

        //넘겨받은 인테트를 바탕으로 액티비티 내용 초기화 작업
        init();

        //변경된 정보를 저장함
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag == 1 || tag == Constant.MARKER_TAG_NEW) {
                    Number number = realm.where(MemoDatabase.class).max("memo_no");
                    if (number == null) number = -1;

                    realm.beginTransaction();
                    //TODO 해당 유저 아이디로 바꿔주기
                    MemoDatabase memoDatabase = realm.createObject(MemoDatabase.class, number.intValue() + 1);
                    memoDatabase.setMemo_own_user_id("guest");
                    memoDatabase.setMemo_createDate(System.currentTimeMillis());

                    memoDatabase.setDataFromKeyworDocuemnt(keywordDocuments);
                    memoDatabase.setMemo_document_place_name(memoTitle.getText().toString());
                    memoDatabase.setMemo_category(TranscHash.spinnerToMarkerTag.get(memoCategory.getSelectedItemPosition()));
                    memoDatabase.setMemo_content(memoContent.getText().toString());
                    memo_no = memoDatabase.getMemo_no();

                    realm.commitTransaction();

                    Intent intent = new Intent();
                    intent.putExtra("addMemoResult", memo_no);
                    setResult(ADD_MEMO_INTENT, intent);
                    Logger.d("정상적으로 저장되었습니다");
                    finish();

                } else {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MemoDatabase memoDatabase = realm.where(MemoDatabase.class).equalTo("memo_no", getIntent.getIntExtra("memo_no", 0)).findFirst();

                            memoDatabase.setMemo_document_place_name(memoTitle.getText().toString());
                            memoDatabase.setMemo_category(TranscHash.spinnerToMarkerTag.get(memoCategory.getSelectedItemPosition()));
                            memoDatabase.setMemo_content(memoContent.getText().toString());
                            memo_no = memoDatabase.getMemo_no();
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent();
                            intent.putExtra("addMemoResult", memo_no);
                            setResult(ADD_MEMO_INTENT, intent);
                            Logger.d("정상적으로 저장되었습니다");
                            finish();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(AddMemoActivity.this, "옛날꺼 업데이트 오류", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void init() {

        if (tag == 1 || tag == Constant.MARKER_TAG_NEW) {
            keywordDocuments = new Gson().fromJson(getIntent.getStringExtra("keywordDocument"), KeywordSearchRepo.KeywordDocuments.class);

            memoTitle.setText(keywordDocuments.getPlace_name());
            if (keywordDocuments.getRoad_address_name().equals("")) {
                memoAddr.setText(keywordDocuments.getAddress_name());
            } else memoAddr.setText(keywordDocuments.getRoad_address_name());

        } else {
            MemoDatabase memoDatabase = realm.where(MemoDatabase.class).equalTo("memo_no", getIntent.getIntExtra("memo_no", 0)).findFirst();

            memoTitle.setText(memoDatabase.getMemo_document_place_name());
            if (memoDatabase.getMemo_document_road_address_name().equals(""))
                memoAddr.setText(memoDatabase.getMemo_document_address_name());
            else memoAddr.setText(memoDatabase.getMemo_document_road_address_name());
            memoCategory.setSelection(TranscHash.markerTagToSpinner.get(memoDatabase.getMemo_category()));
            memoContent.setText(memoDatabase.getMemo_content());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
