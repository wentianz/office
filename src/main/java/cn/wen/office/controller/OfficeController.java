package cn.wen.office.controller;


import cn.wen.office.dto.MessageAutoResponseDTO;
import cn.wen.office.model.User;
import cn.wen.office.service.impl.UserServiceImpl;
import cn.wen.office.service.impl.WeiXinClientImpl;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("/message")
public class OfficeController {

    @Autowired
    private WeiXinClientImpl weiXinClient;

    @Autowired
    private UserServiceImpl userService;


    @Value("${weixin.accessToken}")
    private String accessToken;



    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @PostMapping(value = "/receive2",produces = MediaType.APPLICATION_XML_VALUE)
    public Object  res(@RequestBody JSONObject messageReceiveDTO) throws Exception {
        logger.info("{}",messageReceiveDTO);
        String msgType = messageReceiveDTO.getString("MsgType");
        if(msgType.equals("event")){
            String event = messageReceiveDTO.getString("Event");
            logger.info(event);
            if(event.equals("subscribe")){
                return  subscribe(messageReceiveDTO);
            }
            if(event.equals("CLICK")){
                String eventKey = messageReceiveDTO.getString("EventKey");
                if (eventKey == null){
                    return "success";
                }
                if(eventKey.equals("clockin")){
                    String fromUserName = messageReceiveDTO.getString("FromUserName");
                    return "打卡";
                }
            }
        }
        logger.info("无效");
        return "a";
    }



    private MessageAutoResponseDTO subscribe(JSONObject messageReceiveDTO) throws Exception {
        String fromUserName = messageReceiveDTO.getString("FromUserName");
        JSONObject userInfo = weiXinClient.getUserInfo(accessToken, fromUserName);
        logger.info("{}",userInfo);
        String openId = userInfo.getString("openid");
        if(openId==null|| openId.equals("")){
            throw new Exception("openID is Null, check accessToken");
        }
        User user = new User();
        user.setNickname(userInfo.getString("nickname"));
        user.setOpenid(openId);
        user.setAvatarUrl(userInfo.getString("headimgurl"));
        user.setGender(userInfo.getInteger("sex"));
        userService.create(user);
        logger.info("{}",user);
        MessageAutoResponseDTO messageAutoResponseDTO = new MessageAutoResponseDTO();
        messageAutoResponseDTO.setToUserName(fromUserName);
        messageAutoResponseDTO.setFromUserName(messageReceiveDTO.getString("ToUserName"));
        messageAutoResponseDTO.setCreateTime(new Date().getTime());
        messageAutoResponseDTO.setMsgType("text");
        messageAutoResponseDTO.setContent("你好,"+userInfo.getString("nickname")+" 欢迎订阅文十二");
        return  messageAutoResponseDTO;
    }
}
