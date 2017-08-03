package com.example.jaewanlee.mapmemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.jaewanlee.mapmemo.R;

/**
 * Created by jaewanlee on 2017. 8. 3..
 */

public class LocationBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_base_memo);

        Button mainButton=(Button)findViewById(R.id.locationBase_standard_without);
        Button locationButton=(Button)findViewById(R.id.locationBase_single_task);
        Button timeButton=(Button)findViewById(R.id.locationBase_single_top);
        Button settingButton=(Button)findViewById(R.id.locationBase_single_instance);
        Button faqButton=(Button)findViewById(R.id.locationBase_standard_with);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LocationBaseActivity.class);
                startActivity(intent);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),TimeBaseActivity.class);
                startActivity(intent);
            }
        });



    }
}
