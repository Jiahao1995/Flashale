package com.example.flashale.component;

import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.po.FlashaleActivity;
import com.example.flashale.util.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisPreheatRunner
        implements ApplicationRunner
{
    @Autowired
    RedisService redisService;

    @Autowired
    FlashaleActivityDao flashaleActivityDao;

    @Override
    public void run(ApplicationArguments args)
            throws Exception
    {
        List<FlashaleActivity> flashaleActivities = flashaleActivityDao.queryFlashaleActivityByStatus(1);
        for (FlashaleActivity flashaleActivity : flashaleActivities) {
            redisService.setValue("stock: " + flashaleActivity.getId(), (long) flashaleActivity.getAvailableStock());
        }
    }
}
