package com.example.flashale.db.dao;

import com.example.flashale.db.po.Order;

public interface OrderDao
{
    void insertOrder(Order order);

    Order queryOrder(String orderNo);

    void updateOrder(Order order);
}
