package com.example.flashale.web;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.dao.FlashaleCommodityDao;
import com.example.flashale.db.dao.OrderDao;
import com.example.flashale.db.po.FlashaleActivity;
import com.example.flashale.db.po.FlashaleCommodity;
import com.example.flashale.db.po.Order;
import com.example.flashale.services.FlashaleActivityService;
import com.example.flashale.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FlashaleActivityController
{
    @RequestMapping("/addFlashaleActivity")
    public String addFlashaleActivity()
    {
        return "add_activity";
    }

    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    @RequestMapping("/addFlashaleActivityAction")
    public String addFlashaleActivityAction(
            @RequestParam("name") String name,
            @RequestParam("commodityId") long commodityId,
            @RequestParam("seckillPrice") BigDecimal flashalePrice,
            @RequestParam("oldPrice") BigDecimal oldPrice,
            @RequestParam("seckillNumber") long flashaleNumber,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            Map<String, Object> resultMap
    )
            throws ParseException
    {
        startTime = startTime.substring(0, 10) + startTime.substring(11);
        endTime = endTime.substring(0, 10) + endTime.substring(11);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddhh:mm");
        FlashaleActivity flashaleActivity = new FlashaleActivity();
        flashaleActivity.setName(name);
        flashaleActivity.setCommodityId(commodityId);
        flashaleActivity.setFlashalePrice(flashalePrice);
        flashaleActivity.setOldPrice(oldPrice);
        flashaleActivity.setTotalStock(flashaleNumber);
        flashaleActivity.setAvailableStock(Integer.valueOf("" + flashaleNumber));
        flashaleActivity.setLockStock(0L);
        flashaleActivity.setActivityStatus(1);
        flashaleActivity.setStartTime(format.parse(startTime));
        flashaleActivity.setEndTime(format.parse(endTime));
        flashaleActivityDao.insertFlashaleActivity(flashaleActivity);
        resultMap.put("flashaleActivity", flashaleActivity);
        return "add_success";
    }

    @RequestMapping("/flashale")
    public String activityList(Map<String, Object> resultMap)
    {
        try (Entry entry = SphU.entry("flashale")) {
            List<FlashaleActivity> flashaleActivities = flashaleActivityDao.queryFlashaleActivityByStatus(1);
            resultMap.put("flashaleActivities", flashaleActivities);
            return "seckill_activity";
        }
        catch (BlockException ex) {
            log.error("Flashale activity check is blocked: " + ex.toString());
            return "wait";
        }
    }

    @Autowired
    private FlashaleCommodityDao flashaleCommodityDao;

    @RequestMapping("/item/{flashaleActivityId}")
    public String itemPage(Map<String, Object> resultMap, @PathVariable long flashaleActivityId)
    {
        FlashaleActivity flashaleActivity;
        FlashaleCommodity flashaleCommodity;

        String flashaleActivityInfo = redisService.getValue("flashaleActivity: " + flashaleActivityId);
        if (StringUtils.isNotEmpty(flashaleActivityInfo)) {
            log.info("Redis cache data: " + flashaleActivityInfo);
            flashaleActivity = JSON.parseObject(flashaleActivityInfo, FlashaleActivity.class);
        }
        else {
            flashaleActivity = flashaleActivityDao.queryFlashaleActivityById(flashaleActivityId);
        }

        String flashaleCommodityInfo = redisService.getValue("flashaleCommodity: " + flashaleActivity.getCommodityId());
        if (StringUtils.isNotEmpty(flashaleCommodityInfo)) {
            log.info("Redis cache data: " + flashaleCommodityInfo);
            flashaleCommodity = JSON.parseObject(flashaleActivityInfo, FlashaleCommodity.class);
        }
        else {
            flashaleCommodity = flashaleCommodityDao.queryFlashaleCommodityById(flashaleActivity.getCommodityId());
        }

        resultMap.put("flashaleCommodity", flashaleCommodity);
        resultMap.put("flashalePrice", flashaleActivity.getOldPrice());
        resultMap.put("oldPrice", flashaleActivity.getOldPrice());
        resultMap.put("commodityId", flashaleCommodity.getCommodityName());
        resultMap.put("commodityDesc", flashaleCommodity.getCommodityDesc());
        return "seckill_item";
    }

    @Autowired
    private FlashaleActivityService flashaleActivityService;

    @Resource
    private RedisService redisService;

    @RequestMapping("/flashale/buy/{userId}/{flashaleActivityId}")
    public ModelAndView flashaleCommodity(@PathVariable long userId, @PathVariable long flashaleActivityId)
    {
        boolean stockValidateResult;

        ModelAndView modelAndView = new ModelAndView();
        try {
            // check client's qualification
            if (redisService.isInLimitMember(flashaleActivityId, userId)) {
                modelAndView.addObject("resultInfo", "Sorry, you have already snapped up!");
                modelAndView.setViewName("seckill_result");
                return modelAndView;
            }

            stockValidateResult = flashaleActivityService.flashaleStockValidate(flashaleActivityId);
            if (stockValidateResult) {
                Order order = flashaleActivityService.createOrder(flashaleActivityId, userId);
                modelAndView.addObject("resultInfo", "Snapped up successfully, creating order now, order ID: " + order.getOrderNo());
                modelAndView.addObject("orderNo", order.getOrderNo());
                redisService.addLimitMember(flashaleActivityId, userId);
            }
            else {
                modelAndView.addObject("resultInfo", "Sorry, insufficient stock");
            }
        }
        catch (Exception e) {
            log.error("Error in system: " + e.toString());
            modelAndView.addObject("resultInfo", "snapped up failed");
        }
        modelAndView.setViewName("seckill_result");
        return modelAndView;
    }

    @Autowired
    private OrderDao orderDao;

    @RequestMapping("/flashale/orderQuery/{orderNo}")
    public ModelAndView orderQuery(@PathVariable String orderNo)
    {
        log.info("Order query, order number: " + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        ModelAndView modelAndView = new ModelAndView();
        if (order != null) {
            modelAndView.setViewName("order");
            modelAndView.addObject("order", order);
            FlashaleActivity flashaleActivity = flashaleActivityDao.queryFlashaleActivityById(order.getFlashaleActivityId());
            modelAndView.addObject("flashaleActivity", flashaleActivity);
        }
        else {
            modelAndView.setViewName("order_wait");
        }
        return modelAndView;
    }

    @RequestMapping("/flashale/payOrder/{orderNo}")
    public String payOrder(@PathVariable String orderNo)
            throws Exception
    {
        flashaleActivityService.payOrderProcess(orderNo);
        return "redirect:/flashale/orderQuery/" + orderNo;
    }

    @ResponseBody
    @RequestMapping("/flashale/getSystemTime")
    public String getSystemTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        return date;
    }
}
