package com.example.flashale.db.dao;

import com.example.flashale.db.mappers.FlashaleActivityMapper;
import com.example.flashale.db.po.FlashaleActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import java.util.List;

@Slf4j
@Repository
public class FlashaleActivityDaoImpl
        implements FlashaleActivityDao
{
    @Resource
    private FlashaleActivityMapper flashaleActivityMapper;

    @Override
    public List<FlashaleActivity> queryFlashaleActivityByStatus(int activityStatus)
    {
        return flashaleActivityMapper.queryFlashaleActivityByStatus(activityStatus);
    }

    @Override
    public void insertFlashaleActivity(FlashaleActivity flashaleActivity)
    {
        flashaleActivityMapper.insert(flashaleActivity);
    }

    @Override
    public FlashaleActivity queryFlashaleActivityById(long activityId)
    {
        return flashaleActivityMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateFlashaleActivity(FlashaleActivity flashaleActivity)
    {
        flashaleActivityMapper.updateByPrimaryKey(flashaleActivity);
    }

    @Override
    public boolean lockStock(Long flashaleActivityId)
    {
        int result = flashaleActivityMapper.lockStock(flashaleActivityId);
        if (result < 1) {
            log.error("Stock lock failed!");
            return false;
        }
        return true;
    }

    @Override
    public boolean deductStock(Long flashaleActivityId)
    {
        int result = flashaleActivityMapper.deductStock(flashaleActivityId);
        if (result < 1) {
            log.error("Stock deduct failed");
            return false;
        }
        return true;
    }

    @Override
    public void revertStock(Long flashaleActivityId)
    {
        flashaleActivityMapper.revertStock(flashaleActivityId);
    }
}
