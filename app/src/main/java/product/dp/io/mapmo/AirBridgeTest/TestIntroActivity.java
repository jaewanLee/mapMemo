package product.dp.io.mapmo.AirBridgeTest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.airbridge.AirBridge;
import io.airbridge.deeplink.DeepLink;
import io.fabric.sdk.android.Fabric;
import product.dp.io.mapmo.HomeView.HomeActivity;
import product.dp.io.mapmo.MemoList.MemoListActivity;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Util.Logger;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class TestIntroActivity extends AppCompatActivity {

    Boolean isInstalled;
    Button intro_agree;
    ImageButton service_term;
    ImageButton user_term;
    TextView waitingText;
    ImageView waiting_img;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_intro);

        activity = this;

        intro_agree = (Button) findViewById(R.id.intro_agreed_button);
        service_term = (ImageButton) findViewById(R.id.intro_serviceterm_ImageButton);
        user_term = (ImageButton) findViewById(R.id.intro_userterm_ImageButton);
        waitingText = (TextView) findViewById(R.id.intro_texting_textView);
        waiting_img = (ImageView) findViewById(R.id.intro_image_ImageView);

        isInstalled = isFirstTime();
        if (isInstalled) {

            //처음 시작임
            waitingText.setVisibility(View.INVISIBLE);
            waiting_img.setVisibility(View.INVISIBLE);

            intro_agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveFirstTime();
                    Intent intent = new Intent(TestIntroActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            service_term.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .setShowTitle(false)
                            .setToolbarColor(getResources().getColor(R.color.black))
                            .addDefaultShareMenuItem()
                            .setShowTitle(true)
                            .build();
                    CustomTabsHelper.addKeepAliveExtra(TestIntroActivity.this, customTabsIntent.intent);
                    CustomTabsHelper.openCustomTab(TestIntroActivity.this, customTabsIntent,
                            Uri.parse("https://goo.gl/KNs4Na"),
                            new WebViewFallback());
                }
            });
            user_term.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .setShowTitle(false)
                            .setToolbarColor(getResources().getColor(R.color.black))
                            .addDefaultShareMenuItem()
                            .setShowTitle(true)
                            .build();
                    CustomTabsHelper.addKeepAliveExtra(TestIntroActivity.this, customTabsIntent.intent);
                    CustomTabsHelper.openCustomTab(TestIntroActivity.this, customTabsIntent,
                            Uri.parse("https://goo.gl/vcbHyd"),
                            new WebViewFallback());
                }
            });




        } else {
            //전에 왔던거->바로 넘어가기
            intro_agree.setVisibility(View.INVISIBLE);
            user_term.setVisibility(View.INVISIBLE);
            service_term.setVisibility(View.INVISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (getIntent().getData() != null) {
                        //kakaotalk을 통해 들어온 경우
                        if (getIntent().getDataString() != null && getIntent().getDataString().contains("kakaolink?")) {
                            Intent getIntent = getIntent();
                            Intent intent = new Intent(TestIntroActivity.this, MemoListActivity.class);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setAction(getIntent().getAction());
                            intent.setData(getIntent().getData());
                            startActivity(intent);
                            Logger.d("On Intro Activity");
                            finish();
                        }
                        //airbridge 딥링크
                        else {
                            if (activity != null) {
                                if (DeepLink.hadOpened(activity)) {
                                    Logger.d("airbridge Deeplink");
                                    Intent movingIntent = new Intent(TestIntroActivity.this, HomeActivity.class);
                                    startActivity(movingIntent);
                                    finish();
                                }
                            }
                        }
                    } else {

                        Logger.d("reusing User skip the intro set");
                        Intent intent = new Intent(TestIntroActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
//                        boolean isDauthAgree=getSharedPreferences("Config",MODE_PRIVATE).getBoolean("isDauthAgree",false);
//                        Intent intent=new Intent(TestIntroActivity.this,DauthActivity.class);
//                        startActivity(intent);
//                        finish();


                    }
                }
            }, 2000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean isFirstTime() {
        SharedPreferences firstTimeShared = getSharedPreferences("Config", MODE_PRIVATE);
        return firstTimeShared.getBoolean("isInstalled", true);
    }

    public void saveFirstTime() {
        SharedPreferences firstTimeShared = getSharedPreferences("Config", MODE_PRIVATE);
        SharedPreferences.Editor editor = firstTimeShared.edit();
        editor.putBoolean("isInstalled", false);
        editor.apply();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AirBridge.getTracker().onNewIntent(intent);
    }


}
