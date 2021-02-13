package product.dp.io.mapmo.Menu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import product.dp.io.mapmo.R;

/**
 * Created by jaewanlee on 2018. 4. 29..
 */

public class DauthWallet extends Activity {

    TextView disconnect_tx;
    TextView agree_tx;
    TextView abl_amount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dauth_wallet_layout);

        disconnect_tx = (TextView) findViewById(R.id.dauth_wallet_disconnect);
        agree_tx = (TextView) findViewById(R.id.dauth_wallet_agree);
        abl_amount = (TextView) findViewById(R.id.dauth_wallet_amount);

        int myABLAmount = getSharedPreferences("Config", MODE_PRIVATE).getInt("myABLAmount", 0);
        abl_amount.setText(String.valueOf(myABLAmount));


        disconnect_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences firstTimeShared = getSharedPreferences("Config", MODE_PRIVATE);
                SharedPreferences.Editor editor = firstTimeShared.edit();
                editor.putBoolean("isDauthAgree", false);
                editor.commit();
                finish();

            }
        });
        agree_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }
}
