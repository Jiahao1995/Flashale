package com.example.flashale.db.dao;

import com.example.flashale.db.po.FlashaleActivity;

import java.util.List;

public interface FlashaleActivityDao
{
    public List<FlashaleActivity> queryFlashaleActivityByStatus(int activityStatus);

    public void insertFlashaleActivity(FlashaleActivity flashaleActivity);

    public FlashaleActivity queryFlashaleActivityById(long activityId);

    public void updateFlashaleActivity(FlashaleActivity flashaleActivity);

    public boolean lockStock(Long flashaleActivityId);

    public boolean deductStock(Long flashaleActivityId);

    public void revertStock(Long flashaleActivityId);
}
