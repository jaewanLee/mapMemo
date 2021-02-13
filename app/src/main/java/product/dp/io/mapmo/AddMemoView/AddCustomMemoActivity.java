package product.dp.io.mapmo.AddMemoView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;

import io.realm.Realm;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.Map.KeywordSearchRepo;
import product.dp.io.mapmo.R;

/**
 * Created by jaewanlee on 2017. 12. 28..
 */

public class AddCustomMemoActivity extends AppCompatActivity{

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

    @Override
    public void onCreate( Bundle savedInstanceState) {
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
    }
}
