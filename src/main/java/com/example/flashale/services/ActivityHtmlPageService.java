package com.example.flashale.services;

import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.dao.FlashaleCommodityDao;
import com.example.flashale.db.po.FlashaleActivity;
import com.example.flashale.db.po.FlashaleCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ActivityHtmlPageService
{
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    @Autowired
    private FlashaleCommodityDao flashaleCommodityDao;

    public void createActivityHtml(long flashaleActivityId)
    {
        PrintWriter writer = null;
        try {
            FlashaleActivity flashaleActivity = flashaleActivityDao.queryFlashaleActivityById(flashaleActivityId);
            FlashaleCommodity flashaleCommodity = flashaleCommodityDao.queryFlashaleCommodityById(flashaleActivity.getCommodityId());
            // get page data
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("flashaleActivity", flashaleActivity);
            resultMap.put("flashaleCommodity", flashaleCommodity);
            resultMap.put("flashalePrice", flashaleActivity.getFlashalePrice());
            resultMap.put("oldPrice", flashaleActivity.getOldPrice());
            resultMap.put("commodityId", flashaleActivity.getCommodityId());
            resultMap.put("commodityName", flashaleCommodity.getCommodityName());
            resultMap.put("commodityDesc", flashaleCommodity.getCommodityDesc());

            Context context = new Context();
            context.setVariables(resultMap);

            File file = new File("src/main/resources/templates/" +
                    "seckill_item_" + flashaleActivityId + ".html");
            writer = new PrintWriter(file);
            templateEngine.process("seckill_item", context, writer);
        }
        catch (Exception e) {
            log.error(e.toString());
            log.error("Error in page static: " + flashaleActivityId);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
