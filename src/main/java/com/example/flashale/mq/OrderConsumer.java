package com.example.flashale.mq;

import com.alibaba.fastjson.JSON;
import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.dao.OrderDao;
import com.example.flashale.db.po.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic = "flashale_order", consumerGroup = "flashale_order_group")
public class OrderConsumer
        implements RocketMQListener<MessageExt>
{
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt)
    {
        // parse order request
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Order request received: " + message);
        Order order = JSON.parseObject(message, Order.class);
        order.setCreateTime(new Date());
        // deduct stock
        boolean lockStockResult = flashaleActivityDao.lockStock(order.getFlashaleActivityId());
        if (lockStockResult) {
            order.setOrderStatus(1);
        }
        else {
            order.setOrderStatus(0);
        }
        orderDao.insertOrder(order);
    }
}
