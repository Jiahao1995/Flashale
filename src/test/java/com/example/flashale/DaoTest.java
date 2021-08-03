package com.example.flashale;

import com.example.flashale.db.dao.FlashaleActivityDao;
import com.example.flashale.db.mappers.FlashaleActivityMapper;
import com.example.flashale.db.po.FlashaleActivity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class DaoTest
{
    @Resource
    private FlashaleActivityMapper flashaleActivityMapper;

    @Autowired
    private FlashaleActivityDao flashaleActivityDao;

    @Test
    void flashaleActivityTest()
    {
        FlashaleActivity flashaleActivity = new FlashaleActivity();
        flashaleActivity.setName("test");
        flashaleActivity.setCommodityId(999L);
        flashaleActivity.setTotalStock(100L);
        flashaleActivity.setFlashalePrice(new BigDecimal(99));
        flashaleActivity.setActivityStatus(16);
        flashaleActivity.setOldPrice(new BigDecimal(99));
        flashaleActivity.setAvailableStock(100);
        flashaleActivity.setLockStock(0L);
        flashaleActivityMapper.insert(flashaleActivity);
        System.out.println("====>>>>" + flashaleActivityMapper.selectByPrimaryKey(1L));
    }

    @Test
    void setFlashaleActivityQuery()
    {
        List<FlashaleActivity> flashaleActivities = flashaleActivityDao.queryFlashaleActivityByStatus(0);
        System.out.println(flashaleActivities.size());
        flashaleActivities.stream().forEach(flashaleActivity -> System.out.println(flashaleActivity.toString()));
    }
}
