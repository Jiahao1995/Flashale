package com.example.flashale.db.dao;

import com.example.flashale.db.mappers.OrderMapper;
import com.example.flashale.db.po.Order;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OrderDaoImpl
        implements OrderDao
{
    @Resource
    private OrderMapper orderMapper;

    @Override
    public void insertOrder(Order order)
    {
        orderMapper.insert(order);
    }

    @Override
    public Order queryOrder(String orderNo)
    {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public void updateOrder(Order order)
    {
        orderMapper.updateByPrimaryKey(order);
    }
}
