package com.example.flashale.db.dao;

import com.example.flashale.db.mappers.FlashaleCommodityMapper;
import com.example.flashale.db.po.FlashaleCommodity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class FlashaleCommodityDaoImpl
        implements FlashaleCommodityDao
{
    @Resource
    private FlashaleCommodityMapper flashaleCommodityMapper;

    @Override
    public FlashaleCommodity queryFlashaleCommodityById(long commodityId)
    {
        return flashaleCommodityMapper.selectByPrimaryKey(commodityId);
    }
}
