package com.example.jaewanlee.mapmemo.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.jaewanlee.mapmemo.R;

public class Login extends AppCompatActivity {

    EditText userIdTextView;
    EditText userPasswordTextView;
    ImageButton logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdTextView=(EditText)findViewById(R.id.login_id_editText);
        userPasswordTextView=(EditText)findViewById(R.id.login_pass_editText);
        logInButton=(ImageButton)findViewById(R.id.login_login_button);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

}
