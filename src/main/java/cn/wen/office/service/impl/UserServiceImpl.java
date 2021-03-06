package cn.wen.office.service.impl;

import cn.wen.office.mapper.CheckInOutRecordMapper;
import cn.wen.office.mapper.UserMapper;
import cn.wen.office.model.CheckInOutRecord;
import cn.wen.office.model.User;
import cn.wen.office.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
   @Autowired
   private UserMapper userMapper;
    @Autowired
    private CheckInOutRecordMapper recordMapper;
    @Override
    public void create(User user) {
            userMapper.insert(user);
    }

    @Override
    public List fetchList() {
        return userMapper.selectByExample(null);
    }

    @Override
    public void checkInOut(String fromUserName, Date date,Integer status) {
        CheckInOutRecord checkInOutRecord = new CheckInOutRecord();
        checkInOutRecord.setOpenid(fromUserName);
        checkInOutRecord.setTime(date);
        checkInOutRecord.setState(status);
        recordMapper.insert(checkInOutRecord);
    }
}
