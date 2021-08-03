package com.example.flashale.db.mappers;

import com.example.flashale.db.po.FlashaleCommodity;

public interface FlashaleCommodityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FlashaleCommodity record);

    int insertSelective(FlashaleCommodity record);

    FlashaleCommodity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FlashaleCommodity record);

    int updateByPrimaryKey(FlashaleCommodity record);
}