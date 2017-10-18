package product.dp.io.ab180blog.AddMemoView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.ab180blog.Core.MainApplication;
import product.dp.io.ab180blog.Database.MemoDatabase;
import product.dp.io.ab180blog.Map.KeywordSearchRepo;
import product.dp.io.ab180blog.R;
import product.dp.io.ab180blog.Util.Constant;
import product.dp.io.ab180blog.Util.Logger;

public class AddMemoActivity extends AppCompatActivity {

    EditText memoTitle;
    TextView memoAddr;
    EditText memoContent;
    ImageButton saveButton;

    Realm realm;

    KeywordSearchRepo.KeywordDocuments keywordDocuments;
    MemoDatabase memoDatabase;

    int tag;
    Intent getIntent;
    int memo_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        memoTitle = (EditText) findViewById(R.id.add_memo_title);
        memoAddr = (TextView) findViewById(R.id.add_memo_addr);
        memoContent = (EditText) findViewById(R.id.add_memo_text);
        saveButton = (ImageButton) findViewById(R.id.add_memo_save_imageButton);

        realm = Realm.getDefaultInstance();

        getIntent = getIntent();
        tag = getIntent.getIntExtra("Tag", -1);

        //넘겨받은 인테트를 바탕으로 액티비티 내용 초기화 작업
        init();

        //변경된 정보를 저장함
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        finish();

                    } else {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                memoDatabase = realm.where(MemoDatabase.class).equalTo("memo_no", getIntent.getIntExtra("memo_no", 0)).findFirst();
                                memoDatabase.setMemo_document_place_name(memoTitle.getText().toString());
                                memoDatabase.setMemo_content(memoContent.getText().toString());
                                memo_no = memoDatabase.getMemo_no();
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent();
                                intent.putExtra("addMemoResult", memo_no);
                                setResult(Constant.ADD_MEMO_INTENT, intent);
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

            }
        });

    }

    private void init() {

        if (tag == 1 || tag == Constant.MARKER_TAG_NEW) {
            memoDatabase = new Gson().fromJson(getIntent.getStringExtra("keywordDocument"), MemoDatabase.class);
            memoTitle.setText(memoDatabase.getMemo_document_place_name());
            if (memoDatabase.getMemo_document_road_address_name().equals(""))
                memoAddr.setText(memoDatabase.getMemo_document_address_name());
            else memoAddr.setText(memoDatabase.getMemo_document_road_address_name());

        } else {
            memoDatabase = realm.where(MemoDatabase.class).equalTo("memo_no", getIntent.getIntExtra("memo_no", 0)).findFirst();

            memoTitle.setText(memoDatabase.getMemo_document_place_name());
            if (memoDatabase.getMemo_document_road_address_name().equals(""))
                memoAddr.setText(memoDatabase.getMemo_document_address_name());
            else memoAddr.setText(memoDatabase.getMemo_document_road_address_name());
            memoContent.setText(memoDatabase.getMemo_content());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed())
            realm.close();
    }
}
