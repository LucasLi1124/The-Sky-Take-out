package com.sky.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;


    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */

    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 判断异常情况
        AddressBook addressBook = addressMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 向订单插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());
        orders.setOrderTime(LocalDateTime.now());
        orderMapper.insert(orders);

        // 向订单明细插入多条数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);
        // 清空购物车
        shoppingCartMapper.deleteByIds(userId);
        //封装VO返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();


        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        Integer OrderPaidStatus = Orders.PAID;
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;

        LocalDateTime check_out_time = LocalDateTime.now();

        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态问题");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 查询历史订单
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult historyOrders(int pageNum, int pageSize, Integer status) {
        //设置分页查询的基本条件
        PageHelper.startPage(pageNum, pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        //设置当前用户id
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        //分页查询当前用户的订单信息
        Page<Orders> orders = orderMapper.pagequery(ordersPageQueryDTO);
        //创建一个List 类型 为 VO， 用来接收订单的全部信息并且返回
        List<OrderVO> orderVOS = new ArrayList<>();

        //将查询回来的数据，进行条件判断，如果满足，封装到VO中
        if (orders != null && orders.getTotal() > 0) {
            // 遍历通过分页查询获得的每一个订单信息
            for (Orders orders1 : orders) {

                // 通过遍历订单信息，获取到每一个订单的主键 id
                Long orderId = orders1.getId();

                // 调用orderDetailMapper，通过订单id，查询到每一个订单的订单明细（每道菜品的信息）
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                // 创建一个VO对象，用来传递给前端
                OrderVO orderVO = new OrderVO();

                // 将通过分页查询获得的订单数据里面的信息，拷贝给VO
                BeanUtils.copyProperties(orders1, orderVO);

                // 在将获得的订单明细，设置给VO
                orderVO.setOrderDetailList(orderDetails);

                //最终 VO里面包含了订单数据以及订单明细，在添加到list当中
                orderVOS.add(orderVO);
            }
        }
        //返回封装好的分页数据（总数 + 当前页数）
        return new PageResult(orders.getTotal(), orderVOS);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailsList = orderDetailMapper.getByOrderId(orders.getId());
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailsList);


        return orderVO;
    }


    /**
     * 取消订单
     *
     * @param id
     */
    @Override
    public void cancle(Long id) throws Exception {
        Orders orders = orderMapper.getById(id);
        Integer Status = orders.getStatus();
        if (Status == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (Status > 2) {
            throw new OrderBusinessException("您当前的订单状态为： " + Status + "若要取消需电话沟通商家");

        }
        Orders orders1 = new Orders();
        orders1.setId(orders.getId());
        if (Status == Orders.TO_BE_CONFIRMED) {
//            weChatPayUtil.refund(
//                    orders.getNumber(), //商户订单号
//                    orders.getNumber(), //商户退款单号
//                    new BigDecimal(0.01),//退款金额，单位 元
//                    new BigDecimal(0.01));//原订单金额
                    orders1.setPayStatus(Orders.REFUND);

        }
        orders1.setStatus(Orders.CANCELLED);
        orders1.setCancelTime(LocalDateTime.now());
        orders1.setCancelReason("用户取消");
        orderMapper.update(orders1);


    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        List<ShoppingCart> shoppingCarts =  orderDetails.stream().map(v -> {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .userId(userId)
                    .name(v.getName())
                    .image(v.getImage())
                    .dishId(v.getDishId())
                    .setmealId(v.getSetmealId())
                    .dishFlavor(v.getDishFlavor())
                    .number(v.getNumber())
                    .amount(v.getAmount())
                    .createTime(LocalDateTime.now())
                    .build();
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCarts);
    }


}
