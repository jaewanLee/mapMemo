package com.example.jaewanlee.mapmemo.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Database.UserDatabase;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class Login extends AppCompatActivity {

    EditText userIdTextView;
    EditText userPasswordTextView;
    LoginButton logInButton;

    public UserDatabase _user_db = new UserDatabase();
    private KakaoSessionCallback _kakaoSessionCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdTextView=(EditText)findViewById(R.id.login_id_editText);
        userPasswordTextView=(EditText)findViewById(R.id.login_pass_editText);
        logInButton=(LoginButton)findViewById(R.id.social_login_button);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialLogin();
            }
        });
    }

    // kakao 로그인 프로세스 : 세션 콜백 호출
    private void socialLogin() {
        _kakaoSessionCallback = new KakaoSessionCallback();

        Session.getCurrentSession().addCallback(_kakaoSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, this);

    }

    private class KakaoSessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
//            redirectSignup();
            KakaoRequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
                if(null != exception) {
                    Logger.e(exception.toString());
                }

                setContentView(R.layout.activity_login);
        }
    }

//    protected void redirectSignup() {
//        final Intent next_intent = new Intent(this, SocialSignupActivity.class);
//        next_intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(next_intent);
//        finish();
//
//    }

    protected void KakaoRequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                int _error_code = errorResult.getErrorCode();
                int _network_error_code = -777;


                if(_network_error_code == _error_code) {
                    String _error_message = "카카오톡의 서버 네트워트가 불안정합니다. 다시 시도해 주세요.";
                    Toast.makeText(getApplicationContext(), _error_message, Toast.LENGTH_SHORT).show();

                    return;
                }

                Log.d("Login", errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                setUserDataBase(result);
                redirectLogin();
            }
        });
    }

    protected void setUserDataBase(UserProfile profile) {
        String user_name = profile.getNickname();
        String email = profile.getEmail();
        String thumnail_img_url = profile.getThumbnailImagePath();
        String original_img_url = profile.getProfileImagePath();


        _user_db = new UserDatabase();
        _user_db.setUserName(user_name);
        _user_db.setUserEmail(email);
        _user_db.setUserOriginalImg(original_img_url);
        _user_db.setUserThumnailImg(thumnail_img_url);
    }

    // redirect to 로그인 뷰
    protected void redirectLogin() {
        final Intent next_intent = new Intent(this, Login.class);
        next_intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(next_intent);
        finish();

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }






}
