package com.example.flashale.db.dao;

import com.example.flashale.db.mappers.FlashaleActivityMapper;
import com.example.flashale.db.po.FlashaleActivity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import java.util.List;

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
}
