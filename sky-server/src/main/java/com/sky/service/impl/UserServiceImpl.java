package com.sky.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";


    @Autowired
    private WeChatProperties weChatProperties;


    @Autowired
    private UserMapper userMapper;


    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */

    @Override
    public User login(UserLoginDTO userLoginDTO) {

        String openid = getOpenId(userLoginDTO.getCode());


        //判断openid是否为空，如果为空则登录失败，抛出业务异常
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }


        //判断当前用户是否为新用户
        User user = userMapper.getByOpenId(openid);

        //如果是新用户，自动完成注册
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }




        //返回用户列表
        return user;
    }
    /**
     * 调用微信接口服务获取openid
     * @param code
     * @return
     */

    private String getOpenId(String code){
        //调用微信接口服务，获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json =  HttpClientUtil.doGet(WX_LOGIN,map);
        log.info("微信登录返回的json数据：{}",json);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;

    }
}
