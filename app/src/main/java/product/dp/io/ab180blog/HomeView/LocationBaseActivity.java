package product.dp.io.ab180blog.HomeView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.airbridge.AirBridge;
import io.airbridge.statistics.events.inapp.SignInEvent;
import product.dp.io.ab180blog.Util.Logger;

/**
 * Created by jaewanlee on 2017. 8. 3..
 */

public class LocationBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(product.dp.io.ab180blog.R.layout.activity_location_base_memo);

        AirBridge.getTracker().send(new SignInEvent());

        Button mainButton = (Button) findViewById(product.dp.io.ab180blog.R.id.locationBase_standard_without);
        Button locationButton = (Button) findViewById(product.dp.io.ab180blog.R.id.locationBase_single_task);
        Button timeButton = (Button) findViewById(product.dp.io.ab180blog.R.id.locationBase_single_top);
        Button settingButton = (Button) findViewById(product.dp.io.ab180blog.R.id.locationBase_single_instance);
        Button faqButton = (Button) findViewById(product.dp.io.ab180blog.R.id.locationBase_standard_with);
        Button accessButton=(Button)findViewById(product.dp.io.ab180blog.R.id.locationBase_recent_screen);

        Intent intent=getIntent();
        String deepLinkUri=intent.getData().getPath();
        String parsedData=deepLinkUri.substring(1);
        Logger.d(parsedData);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationBaseActivity.class);
                startActivity(intent);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TimeBaseActivity.class);
                startActivity(intent);
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),FAQActivity.class);
                startActivity(intent);
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
