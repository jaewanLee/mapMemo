package product.dp.io.mapmo.Core;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;

import product.dp.io.mapmo.HomeView.HomeActivity;
import product.dp.io.mapmo.R;

/**
 * Created by jaewanlee on 2018. 4. 29..
 */

public class DauthActivity extends Activity {

    TextView postponeDatu_tv;
    TextView agreeDauth_tv;


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dauth_permission_layout);

        postponeDatu_tv=(TextView)findViewById(R.id.dauth_postpone_textView);
        agreeDauth_tv=(TextView)findViewById(R.id.dauth_agree_textView);

        postponeDatu_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DauthActivity.this, "나중에 사용해 봐라", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DauthActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        agreeDauth_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> eventValue=new HashMap<String,Object>();
                eventValue.put("af_complete_registration","Dauth Register");
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.COMPLETE_REGISTRATION,eventValue);


                SharedPreferences firstTimeShared = getSharedPreferences("Config", MODE_PRIVATE);
                SharedPreferences.Editor editor = firstTimeShared.edit();
                editor.putBoolean("isDauthAgree", true);
                editor.commit();

                Intent intent=new Intent(DauthActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
