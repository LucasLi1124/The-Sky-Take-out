package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询历史订单
     *
     * @param pageSize
     * @param page
     * @param status
     * @return
     */

    PageResult historyOrders(int page, int pageSize, Integer status);

    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    OrderVO orderDetail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancle(Long id) throws Exception;

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);
}
