package com.example.flashale.mq;

import com.alibaba.fastjson.JSON;
import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.dao.OrderDao;
import com.example.flashale.db.po.Order;
import com.example.flashale.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "pay_check", consumerGroup = "pay_check_group")
public class PayStatusCheckListener
        implements RocketMQListener<MessageExt>
{
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    @Resource
    private RedisService redisService;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt)
    {
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Order payment check message received: " + message);
        Order order = JSON.parseObject(message, Order.class);
        // 1. query order
        Order orderInfo = orderDao.queryOrder(order.getOrderNo());
        // 2. check if order is paid
        if (orderInfo.getOrderStatus() != 2) {
            // 3. unfinished payment
            log.info("Payment not finished, order number: " + orderInfo.getOrderNo());
            orderInfo.setOrderStatus(99);
            orderDao.updateOrder(orderInfo);
            // 4. revert database stock
            flashaleActivityDao.revertStock(order.getFlashaleActivityId());
            // revert redis stock
            redisService.revertStock("Stock: " + order.getFlashaleActivityId());
            // 5. remove from client list
            redisService.removeLimitMember(order.getFlashaleActivityId(), order.getUserId());
        }
    }
}
