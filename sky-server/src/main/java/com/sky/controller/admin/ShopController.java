package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺状态相关接口")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String key = "SHOP_STATUS";

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态：{}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(key, status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status =(Integer) redisTemplate.opsForValue().get(key);
        log.info("获取店铺状态：{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);

    }
}
