package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface  OrderMapper {


    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);


    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);
    /**
     * 用于替换微信支付更新数据库状态的问题
     * @param orderStatus
     * @param orderPaidStatus
     */

    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, checkout_time = #{check_out_time} where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, String orderNumber);

    /**
     * 查询历史订单
     * @param pageQueryDTO
     * @return
     */
    Page<Orders> pagequery(OrdersPageQueryDTO pageQueryDTO);

    /**
     * 根据id查询订单信息
     *
     * @param id
     * @return
     */
    @Select("SELECT * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 各个状态的订单数量统计
     * @param confirmed
     * @return
     */


    @Select("select count(id) from orders where status = #{status}")
    Integer countByStatus(Integer confirmed);

    /**
     * 处理超时订单
     * @param status
     * @param orderTime
     * @return
     */


    @Select("SELECT * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 营业额统计
     * @param map
     * @return
     */

    Double sumByMap(Map map);

    /**
     * 订单统计
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
