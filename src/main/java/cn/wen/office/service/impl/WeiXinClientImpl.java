package cn.wen.office.service.impl;

import cn.wen.office.api.WeiXinApi;
import cn.wen.office.service.WeiXinClient;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class WeiXinClientImpl implements WeiXinClient {

    private WeiXinApi weiXinApi;

    @Value("${mp.AppId}")
    private String appId;

    @Value("${mp.secret}")
    private String secret;
    @Override
    public JSONObject getSnsAccessToken(String code) throws IOException {
        return null;
    }

    @Override
    public JSONObject getSnsUserInfo(String access_token, String openid) throws IOException {
        return null;
    }

    @Override
    public JSONObject getUserInfo(String access_token, String openid) throws IOException {
        Call<JSONObject> call = weiXinApi.getUserInfo(access_token, openid, "zh_CN");
        Response<JSONObject> response = call.execute();
        return response.body();
    }
}
