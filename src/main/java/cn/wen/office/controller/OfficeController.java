package cn.wen.office.controller;


import cn.wen.office.common.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/message")
public class OfficeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @PostMapping("/receive2")
    public String  res(@RequestParam Map<String,String> allParams){
            logger.info("{}",allParams);
            String echostr = allParams.get("echostr");
            return echostr;
    }
}
