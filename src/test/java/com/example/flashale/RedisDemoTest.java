package com.example.flashale;

import com.example.flashale.services.FlashaleActivityService;
import com.example.flashale.util.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.UUID;

@SpringBootTest
public class RedisDemoTest
{
    @Resource
    private RedisService redisService;

    @Test
    public void stockTest()
    {
        redisService.setValue("stock:34", 10L);
    }

    @Test
    public void getStockTest()
    {
        String stock = redisService.getValue("stock:34");
        System.out.println(stock);
    }

    @Test
    public void stockDeductValidatorTest()
    {
        boolean result = redisService.stockDeductValidator("stock:34");
        System.out.println("result: " + result);
        String stock = redisService.getValue("stock:12");
        System.out.println("stock: " + stock);
    }

    @Autowired FlashaleActivityService flashaleActivityService;

    @Test
    public void pushFlashaleInfoToRedisTest()
    {
        flashaleActivityService.pushFlashaleInfoToRedis(38);
    }

    @Test
    public void testConcurrentAddLock()
    {
        for (int i = 0; i < 10; i++) {
            String requestId = UUID.randomUUID().toString();
            System.out.println(redisService.tryGetDistributedLock("A", requestId, 1000));
        }
    }

    @Test
    public void testConcurrent()
    {
        for (int i = 0; i < 10; i++) {
            String requestId = UUID.randomUUID().toString();
            System.out.println(redisService.tryGetDistributedLock("A", requestId, 1000));
            redisService.releaseDistributedLock("A", requestId);
        }
    }
}
