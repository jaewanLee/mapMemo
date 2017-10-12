package product.dp.io.ab180blog.Shared;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import product.dp.io.ab180blog.Util.Constant;
import product.dp.io.ab180blog.Util.Logger;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class SearchLocation {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    private static SearchLocation instance;
    OkHttpClient okHttpClient;
    private String KAKAO_SEARCH_URL="https://dapi.kakao.com/v2/local/search/address.json";

    public static SearchLocation getInstance() {
        if(instance==null){
            instance=new SearchLocation();
        }
        return  instance;
    }

    private SearchLocation(){
            okHttpClient=new OkHttpClient();
    }

    public String run(String curl){

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request request=new Request.Builder().addHeader("Authorization", Constant.DAUME_SEARCH_API_KEY)
                                .get().url("https://dapi.kakao.com/v2/local/search/keyword.json?y=37.514322572335935&x=127.06283102249932&radius=20000&query=tomntoms").build();
                        Response response =okHttpClient.newCall(request).execute();
                        String abce=response.body().string();
                        String adbf=response.body().toString();
                        Logger.d(response.networkResponse().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

            return  "hello";
    }

    private String makeJsonBody(String query){
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("query",query);
            return jsonObject.toString();
        } catch (JSONException e) {
            Logger.wtf("make json body err", e);
            return null;
        }
    }

    private String makeGetUrl(String query){
        String requestUrl="https://dapi.kakao.com/v2/local/search/keyword.json?"+"?query=";
        try {
            return  requestUrl+java.net.URLEncoder.encode(new String("영등포 탐앤탐스".getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "not url changed";
        }
    }

}
