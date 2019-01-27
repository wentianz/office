package cn.wen.office.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

public interface WeiXinClient {
    JSONObject getSnsAccessToken(String code) throws IOException;

    JSONObject getSnsUserInfo(String access_token, String openid) throws IOException;

    JSONObject getUserInfo(String access_token, String openid) throws IOException;

}
