package cn.wen.office.controller;


import cn.wen.office.dto.MessageAutoResponseDTO;
import cn.wen.office.model.User;
import cn.wen.office.service.impl.UserServiceImpl;
import cn.wen.office.service.impl.WeiXinClientImpl;
import cn.wen.office.util.WeixinAccessTokenTask;
import com.alibaba.fastjson.JSONObject;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/message")
public class OfficeController {

    @Autowired
    private WeiXinClientImpl weiXinClient;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    WeixinAccessTokenTask weixinAccessTokenTask;


    @Value("${checkInOut.latitude}")
    private Double checkLatitude;
    @Value("${checkInOut.longitude}")
    private Double checkLongitude;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(value = "/receive2", produces = MediaType.APPLICATION_XML_VALUE)
    public Object res(@RequestBody JSONObject messageReceiveDTO) throws Exception {
        logger.info("{}", messageReceiveDTO);
        String msgType = messageReceiveDTO.getString("MsgType");
        if (msgType.equals("event")) {
            String event = messageReceiveDTO.getString("Event");
            logger.info(event);
            //事件为订阅执行该方法
            if (event.equals("subscribe")) {
                //用户订阅
                return subscribe(messageReceiveDTO);
            }
            //事件为获取用户地理位置
            if (event.equals("LOCATION")) {
                return getLocation(messageReceiveDTO);
            }
            //用户点击菜单
            if (event.equals("CLICK")) {
                String eventKey = messageReceiveDTO.getString("EventKey");
                if (eventKey == null) {
                    return "success";
                }
                if (eventKey.equals("clockin")) {
                    return checkInOut(messageReceiveDTO);
                }
            }
        }
        logger.info("无效");
        return "success";
    }
    private Object getLocation(@RequestBody JSONObject messageReceiveDTO) {
        String fromUserName = messageReceiveDTO.getString("FromUserName");
        Double latitude = messageReceiveDTO.getDouble("Latitude");
        Double longitude = messageReceiveDTO.getDouble("Longitude");
        JSONObject position = new JSONObject();
        position.put("latitude", latitude);
        position.put("longitude", longitude);
        String postionUserKey = "position" + fromUserName;
        redisTemplate.opsForHash().putAll(postionUserKey, position);
        redisTemplate.expire(postionUserKey, 50000, TimeUnit.MILLISECONDS);
        return "success";
    }
    private MessageAutoResponseDTO checkInOut(@RequestBody JSONObject messageReceiveDTO) {
        String fromUserName = messageReceiveDTO.getString("FromUserName");
        //判断当天是否已经打卡
        String clockInLimit="clockInLimit"+fromUserName;
        String  status = (String) redisTemplate.opsForValue().get(clockInLimit);
        if( status == null){
            MessageAutoResponseDTO autoResponseDTO = getMessageAutoResponseDTO(messageReceiveDTO, fromUserName);
            autoResponseDTO.setContent("该时间段已打卡");
            return  autoResponseDTO;
        }
        String positionUserKey = "position" + fromUserName;
        Double latitude = (Double) redisTemplate.opsForHash().get(positionUserKey, "latitude");
        Double longitude = (Double) redisTemplate.opsForHash().get(positionUserKey, "longitude");
        logger.info("{}", latitude, longitude);
        DegreeCoordinate lat = Coordinate.fromDegrees(latitude);
        DegreeCoordinate lng = Coordinate.fromDegrees(longitude);
        //用户所在位置
        Point userCurrentPostion = Point.at(lat, lng);

        lat = Coordinate.fromDegrees(checkLatitude);
        lng = Coordinate.fromDegrees(checkLongitude);
        //规定打卡位置
        Point checkPostion = Point.at(lat, lng);

        //位置距离 单位为 m
        double distance = EarthCalc.harvesineDistance(userCurrentPostion, checkPostion);

        if (distance > 100.00D) {
            MessageAutoResponseDTO messageAutoResponseDTO = getMessageAutoResponseDTO(messageReceiveDTO, fromUserName);
            messageAutoResponseDTO.setContent("不在打卡范围、换个位置试试");
            return messageAutoResponseDTO;
        }
        Date now = new Date();
        LocalTime time = now.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime onWorkStart = LocalTime.parse("08:00:00");
        LocalTime onWorkEnd = LocalTime.parse("12:00:00");
        LocalTime offWorkStart = LocalTime.parse("14:00:00");
        LocalTime offWorkEnd = LocalTime.parse("19:00:00");
        String content = "";
        if (time.isAfter(onWorkStart) && time.isBefore(onWorkEnd)) {
            content = new Date() + "上班打卡成功+Ծ‸ Ծ ";
            userService.checkInOut(fromUserName, new Date(), 0);
            redisTemplate.opsForValue().set("clockInLimit"+fromUserName,fromUserName);
            redisTemplate.expire("clockInLimit"+fromUserName,60,TimeUnit.MINUTES);
        } else if (time.isAfter(offWorkStart) && time.isBefore(offWorkEnd)) {
            content = new Date() + "下班打卡成功+ค(TㅅT)";
            userService.checkInOut(fromUserName, new Date(), 1);
            redisTemplate.opsForValue().set("clockInLimit"+fromUserName,fromUserName);
            redisTemplate.expire("clockInLimit"+fromUserName,60,TimeUnit.MINUTES);
        } else {
            content = "不在打卡时间内、有点早哦";
        }
        MessageAutoResponseDTO messageAutoResponseDTO = new MessageAutoResponseDTO();
        messageAutoResponseDTO.setToUserName(fromUserName);
        String toUserName = messageReceiveDTO.getString("ToUserName");
        messageAutoResponseDTO.setFromUserName(toUserName);
        messageAutoResponseDTO.setCreateTime(new Date().getTime());
        messageAutoResponseDTO.setMsgType("text");
        messageAutoResponseDTO.setContent(content);
        return messageAutoResponseDTO;
    }

    private MessageAutoResponseDTO getMessageAutoResponseDTO(@RequestBody JSONObject messageReceiveDTO, String fromUserName) {
        MessageAutoResponseDTO messageAutoResponseDTO = new MessageAutoResponseDTO();
        messageAutoResponseDTO.setToUserName(fromUserName);
        messageAutoResponseDTO.setFromUserName(messageReceiveDTO.getString("ToUserName"));
        messageAutoResponseDTO.setCreateTime(new Date().getTime());
        messageAutoResponseDTO.setMsgType("text");
        return messageAutoResponseDTO;
    }


    private MessageAutoResponseDTO subscribe(JSONObject messageReceiveDTO) throws Exception {
        String fromUserName = messageReceiveDTO.getString("FromUserName");
        JSONObject userInfo = weiXinClient.getUserInfo(weixinAccessTokenTask.getAccessToken(), fromUserName);
        logger.info("{}", userInfo);
        String openId = userInfo.getString("openid");
        if (openId == null || openId.equals("")) {
            throw new Exception("openID is Null, check accessToken");
        }
        User user = new User();
        //因数据存储可能乱码 对数据进行编码处理
        String nickname = URLEncoder.encode(userInfo.getString("nickname"), "utf-8");
        user.setNickname(nickname);
        user.setOpenid(openId);
        user.setAvatarUrl(userInfo.getString("headimgurl"));
        user.setGender(userInfo.getInteger("sex"));
        userService.create(user);
        logger.info("{}", user);
        MessageAutoResponseDTO messageAutoResponseDTO = getMessageAutoResponseDTO(messageReceiveDTO, fromUserName);
        messageAutoResponseDTO.setContent("你好," + userInfo.getString("nickname") + " 欢迎订阅文十二 \n 若要使用打卡功能请先开启位置服务");
        return messageAutoResponseDTO;
    }
}
