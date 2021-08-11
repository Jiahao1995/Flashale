package com.example.flashale.db.mappers;

import com.example.flashale.db.po.FlashaleActivity;

import java.util.List;

public interface FlashaleActivityMapper
{
    int deleteByPrimaryKey(Long id);

    int insert(FlashaleActivity record);

    int insertSelective(FlashaleActivity record);

    FlashaleActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FlashaleActivity record);

    int updateByPrimaryKey(FlashaleActivity record);

    List<FlashaleActivity> queryFlashaleActivityByStatus(int activityStatus);

    int lockStock(Long id);

    int deductStock(Long id);

    void revertStock(Long id);
}