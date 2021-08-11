package com.example.flashale.mq;

import com.alibaba.fastjson.JSON;
import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.po.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "pay_done", consumerGroup = "pay_done_group")
public class PayDoneConsumer
        implements RocketMQListener<MessageExt>
{
    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    @Override
    public void onMessage(MessageExt messageExt)
    {
        // 1. parse create order request
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Create order request received: " + message);
        Order order = JSON.parseObject(message, Order.class);
        // 2. deduct stock
        flashaleActivityDao.deductStock(order.getFlashaleActivityId());
    }
}
