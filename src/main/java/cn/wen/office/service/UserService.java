package cn.wen.office.service;

import cn.wen.office.model.User;

import java.util.Date;
import java.util.List;

public interface UserService {

    void create(User user);

    List fetchList();

    void checkInOut(String fromUserName, Date date, Integer status);
}
