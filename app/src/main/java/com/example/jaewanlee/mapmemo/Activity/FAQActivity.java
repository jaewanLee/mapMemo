package com.example.jaewanlee.mapmemo.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.jaewanlee.mapmemo.R;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Button mainButton = (Button) findViewById(R.id.faq_standard_without);
        Button locationButton = (Button) findViewById(R.id.faq_single_task);
        Button timeButton = (Button) findViewById(R.id.faq_single_top);
        Button settingButton = (Button) findViewById(R.id.faq_single_instance);
        Button faqButton = (Button) findViewById(R.id.faq_standard_with);
        Button accessButton=(Button)findViewById(R.id.faq_recent_screen);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationBaseActivity.class);
                startActivity(intent);
                finish();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TimeBaseActivity.class);
                startActivity(intent);
                finish();
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),FAQActivity.class);
                startActivity(intent);
                finish();
            }
        });

        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccessTermActivity.class);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(intent);

            }
        });
    }
}
