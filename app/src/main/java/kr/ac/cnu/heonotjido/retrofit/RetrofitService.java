package kr.ac.cnu.heonotjido.retrofit;

import kr.ac.cnu.heonotjido.gson.GeoCode;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RetrofitService {

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("geocode")
    Call<GeoCode> geoCode(@Header("X-Naver-Client-Id") String clientId,
                        @Header("X-Naver-Client-Secret") String clientSecret,
                        @Query("query") String address);
}
