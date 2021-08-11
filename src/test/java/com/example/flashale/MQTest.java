package com.example.flashale;

import com.example.flashale.mq.RocketMQService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MQTest
{
    @Autowired RocketMQService rocketMQService;

    @Test
    public void sendMQTest()
            throws Exception
    {
        rocketMQService.sendMessage("test-flashale", "Hello World" + new Date());
    }
}
