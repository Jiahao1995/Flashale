package com.example.flashale.services;

import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.po.FlashaleActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlashaleOversellService
{
    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    public String processFlashale(long activityId)
    {
        FlashaleActivity flashaleActivity = flashaleActivityDao.queryFlashaleActivityById(activityId);
        long availableStock = flashaleActivity.getAvailableStock();
        String result;
        if (availableStock > 0) {
            result = "Congratulations, snapped up successfully!";
            System.out.println(result);
            availableStock -= 1;
            flashaleActivity.setAvailableStock(Integer.valueOf("" + availableStock));
            flashaleActivityDao.updateFlashaleActivity(flashaleActivity);
        }
        else {
            result = "Sorry, the product is snapped up!";
            System.out.println(result);
        }
        return result;
    }
}
