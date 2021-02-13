package product.dp.io.mapmo.AddMemoView;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by jaewanlee on 2017. 12. 28..
 */

public interface LatLngSearchInterface {
    @Headers("Authorization: KakaoAK 13154e096c55ada7691f4e2df3c5c9f4")
    @GET("/v2/local/geo/coord2address.json")
    Call<LatLngSearchRepo> getLatLngSearchRepo(
            @Query("x") String x,
            @Query("y") String y
    );

    public static final Retrofit retrofit=new Retrofit.Builder().baseUrl("https://dapi.kakao.com").addConverterFactory(GsonConverterFactory.create()).build();

}
