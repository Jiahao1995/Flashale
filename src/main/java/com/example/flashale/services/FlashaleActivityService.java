package com.example.flashale.services;

import com.alibaba.fastjson.JSON;
import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.dao.FlashaleCommodityDao;
import com.example.flashale.db.dao.OrderDao;
import com.example.flashale.db.po.FlashaleActivity;
import com.example.flashale.db.po.FlashaleCommodity;
import com.example.flashale.db.po.Order;
import com.example.flashale.mq.RocketMQService;
import com.example.flashale.util.RedisService;
import com.example.flashale.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class FlashaleActivityService
{
    @Autowired
    private RedisService redisService;

    public boolean flashaleStockValidate(Long activityId)
    {
        String key = "stock: " + activityId;
        return redisService.stockDeductValidator(key);
    }

    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    @Autowired
    private RocketMQService rocketMQService;

    private SnowFlake snowFlake = new SnowFlake(1, 1);

    public Order createOrder(long flashaleActivityId, long userId)
            throws Exception
    {
        FlashaleActivity flashaleActivity = flashaleActivityDao.queryFlashaleActivityById(flashaleActivityId);
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setFlashaleActivityId(flashaleActivity.getId());
        order.setUserId(userId);
        order.setOrderAmount(flashaleActivity.getFlashalePrice().longValue());
        rocketMQService.sendMessage("flashale_order", JSON.toJSONString(order));
        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order), 3);
        return order;
    }

    @Autowired
    private OrderDao orderDao;

    public void payOrderProcess(String orderNo)
            throws Exception
    {
        log.info("Order payment finished, order number: " + orderNo);
        Order order = orderDao.queryOrder(orderNo);

        if (order == null) {
            log.error("Order number does not exist: " + orderNo);
            return;
        }
        else if (order.getOrderStatus() != 1) {
            log.error("Invalid Order status: " + orderNo);
            return;
        }

        order.setPayTime(new Date());
        order.setOrderStatus(2);
        orderDao.updateOrder(order);

        rocketMQService.sendMessage("pay_done", JSON.toJSONString(order));
    }

    @Autowired
    private FlashaleCommodityDao flashaleCommodityDao;

    public void pushFlashaleInfoToRedis(long flashaleActivityId)
    {
        FlashaleActivity flashaleActivity = flashaleActivityDao.queryFlashaleActivityById(flashaleActivityId);
        redisService.setValue("flashaleActivity: " + flashaleActivityId,
                JSON.toJSONString(flashaleActivity));

        FlashaleCommodity flashaleCommodity = flashaleCommodityDao.queryFlashaleCommodityById(flashaleActivity.getCommodityId());
        redisService.setValue("flashaleCommodity: " + flashaleActivity.getCommodityId(),
                JSON.toJSONString(flashaleCommodity));
    }
}
