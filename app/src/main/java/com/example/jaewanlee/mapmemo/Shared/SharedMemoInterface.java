package com.example.jaewanlee.mapmemo.Shared;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by jaewanlee on 2017. 9. 13..
 */

public interface SharedMemoInterface {

    @POST("")
    Call<String> getResult(@Query("memoGID") String memoGID);
}
