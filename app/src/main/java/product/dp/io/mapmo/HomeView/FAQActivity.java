package product.dp.io.mapmo.HomeView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(product.dp.io.mapmo.R.layout.activity_faq);

        Button mainButton = (Button) findViewById(product.dp.io.mapmo.R.id.faq_standard_without);
        Button locationButton = (Button) findViewById(product.dp.io.mapmo.R.id.faq_single_task);
        Button timeButton = (Button) findViewById(product.dp.io.mapmo.R.id.faq_single_top);
        Button settingButton = (Button) findViewById(product.dp.io.mapmo.R.id.faq_single_instance);
        Button faqButton = (Button) findViewById(product.dp.io.mapmo.R.id.faq_standard_with);
        Button accessButton=(Button)findViewById(product.dp.io.mapmo.R.id.faq_recent_screen);

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
