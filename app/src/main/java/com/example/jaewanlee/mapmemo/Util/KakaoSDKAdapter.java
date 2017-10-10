package com.example.jaewanlee.mapmemo.Util;

import android.content.Context;

import com.example.jaewanlee.mapmemo.Core.MainApplication;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;

/**
 * Created by tim on 27/09/2017.
 */

public class KakaoSDKAdapter extends KakaoAdapter {

    @Override
    public ISessionConfig getSessionConfig() {
        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
            }

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            @Override
            public boolean isSecureMode() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return false;
            }
        };
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
        return new IApplicationConfig() {

//            @Override
//            public Activity getTopActivity() {
//                return MainApplication.setCurrentActivity();
//            }

            @Override
            public Context getApplicationContext() {
                return MainApplication.getMainApplicationContext();
            }


        };
    }
}
