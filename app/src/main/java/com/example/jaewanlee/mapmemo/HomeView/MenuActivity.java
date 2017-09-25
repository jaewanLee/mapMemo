package com.example.jaewanlee.mapmemo.HomeView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jaewanlee.mapmemo.LockScreen.Service.ScreenService;
import com.example.jaewanlee.mapmemo.R;


public class MenuActivity extends AppCompatActivity {

    ImageButton userImage;
    TextView userName;
    TextView userId;
    Button cancelButton;
    Button okButton;
    //TODO typeSomething 버튼들 만들기
    Switch lockScreen;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        userImage = (ImageButton) findViewById(R.id.menu_imgae_ImageButton);
        userName = (TextView) findViewById(R.id.menu_userName_TextView);
        userId = (TextView) findViewById(R.id.menu_userId_TextView);
        cancelButton = (Button) findViewById(R.id.menu_cancle_button);
        okButton = (Button) findViewById(R.id.menu_ok_button);
        lockScreen = (Switch) findViewById(R.id.menu_Lockscreen_Switch);

        sharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);

        Glide.with(this).load(R.drawable.glide_testing_image).apply(RequestOptions.circleCropTransform()).into(userImage);

        lockScreen.setChecked(sharedPreferences.getBoolean("lockScreen",false));

        lockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(b){
                    editor.putBoolean("lockScreen",true);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    startService(intent);
                }else{
                    editor.putBoolean("lockScreen",false);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    stopService(intent);
                }

            }
        });


    }
}
