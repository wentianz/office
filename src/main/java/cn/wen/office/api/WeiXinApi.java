package cn.wen.office.api;


import com.alibaba.fastjson.JSONObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeiXinApi{
    @GET("https://api.weixin.qq.com/cgi-bin/user/info")
    Call<JSONObject> getUserInfo(@Query("access_token") String access_token, @Query("openid") String openid, @Query("lang") String lang);

    @GET("https://api.weixin.qq.com/cgi-bin/token")
    Call<JSONObject> getAccessToken(@Query("appid") String appid, @Query("secret") String secret,@Query("grant_type") String grant_type);
}
