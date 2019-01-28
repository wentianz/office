package cn.wen.office.util;


import cn.wen.office.api.WeiXinApi;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * 该类为动态的过去access_token 所使用
 */
@Component
public class WeixinAccessTokenTask {
 /*   @Autowired
    RedisTemplate redisTemplate;*/
    @Value("${mp.appId}")
    private String appId;
    @Value("${mp.secret}")
    private String secret;


    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    Logger logger = LoggerFactory.getLogger(WeixinAccessTokenTask.class);
    private Retrofit retrofit;
    private WeiXinApi weiXinApi;
    public WeixinAccessTokenTask(@Value("${weixin.baseUrl}") String url) throws MalformedURLException {
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        weiXinApi = retrofit.create(WeiXinApi.class);
    }
    // 第一次延迟1秒执行，当执行完后5400秒再执行
    @Scheduled(initialDelay = 1000, fixedDelay = 5600*1000)
    public void getWeiXinAccessToken() throws IOException {
        Call<JSONObject> token = weiXinApi.getAccessToken(appId, secret, "client_credential");
        JSONObject body = token.execute().body();
        logger.info("获取到的微信access_token为"+body.getString("access_token"));
        accessToken=body.getString("access_token");
    }
}
