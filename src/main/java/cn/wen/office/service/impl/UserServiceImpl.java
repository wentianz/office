package cn.wen.office.service.impl;

import cn.wen.office.mapper.UserMapper;
import cn.wen.office.model.User;
import cn.wen.office.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
   @Autowired
   private UserMapper userMapper;

    @Override
    public void create(User user) {
            userMapper.insert(user);
    }
}
