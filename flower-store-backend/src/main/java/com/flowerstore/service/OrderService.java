package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.Order;
import com.flowerstore.entity.OrderItem;
import com.flowerstore.mapper.OrderItemMapper;
import com.flowerstore.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductService productService;

    /**
     * 分页查询订单列表
     */
    public Page<Order> getPage(Integer current, Integer size, String orderNo, Integer status, Long userId) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(page, wrapper);
    }

    /**
     * 根据用户ID查询订单列表（带商品信息）
     */
    public List<Map<String, Object>> getListByUserId(Long userId, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        List<Order> orders = orderMapper.selectList(wrapper);
        
        // 为每个订单添加商品信息
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("orderNo", order.getOrderNo());
            orderMap.put("userId", order.getUserId());
            orderMap.put("receiverName", order.getReceiverName());
            orderMap.put("receiverPhone", order.getReceiverPhone());
            orderMap.put("province", order.getProvince());
            orderMap.put("city", order.getCity());
            orderMap.put("district", order.getDistrict());
            orderMap.put("address", order.getAddress());
            orderMap.put("totalPrice", order.getTotalPrice());
            orderMap.put("freight", order.getFreight());
            orderMap.put("actualPayment", order.getActualPayment());
            orderMap.put("paymentMethod", order.getPaymentMethod());
            orderMap.put("remark", order.getRemark());
            orderMap.put("status", order.getStatus());
            orderMap.put("payTime", order.getPayTime());
            orderMap.put("deliveryTime", order.getDeliveryTime());
            orderMap.put("finishTime", order.getFinishTime());
            orderMap.put("createTime", order.getCreateTime());
            orderMap.put("updateTime", order.getUpdateTime());
            
            // 查询订单商品
            List<OrderItem> items = getOrderItems(order.getId());
            orderMap.put("items", items);
            
            return orderMap;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据ID查询订单
     */
    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    /**
     * 根据订单号查询订单
     */
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return orderMapper.selectOne(wrapper);
    }

    /**
     * 根据订单ID查询订单明细
     */
    public List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(wrapper);
    }

    /**
     * 创建订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order, List<Map<String, Object>> items) {
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(1); // 待付款

        // 计算订单金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer count = Integer.valueOf(item.get("count").toString());
            BigDecimal price = new BigDecimal(item.get("price").toString());
            totalPrice = totalPrice.add(price.multiply(new BigDecimal(count)));
        }
        
        order.setTotalPrice(totalPrice);
        
        // 计算运费（满99免运费）
        BigDecimal freight = totalPrice.compareTo(new BigDecimal("99")) >= 0 
                ? BigDecimal.ZERO : new BigDecimal("8");
        order.setFreight(freight);
        
        // 计算实付金额
        order.setActualPayment(totalPrice.add(freight));

        // 保存订单
        orderMapper.insert(order);

        // 保存订单明细
        for (Map<String, Object> item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(Long.valueOf(item.get("productId").toString()));
            orderItem.setProductName(item.get("productName").toString());
            orderItem.setProductImage(item.get("productImage").toString());
            orderItem.setPrice(new BigDecimal(item.get("price").toString()));
            orderItem.setCount(Integer.valueOf(item.get("count").toString()));
            orderItem.setSubtotal(orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount())));
            orderItemMapper.insert(orderItem);

            // 更新商品库存和销量
            productService.updateInventory(orderItem.getProductId(), orderItem.getCount());
        }

        return order;
    }

    /**
     * 更新订单状态
     */
    public void updateStatus(Long id, Integer status) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            order.setStatus(status);
            
            // 根据状态更新相应的时间
            if (status == 2) {
                order.setPayTime(LocalDateTime.now());
            } else if (status == 3) {
                order.setDeliveryTime(LocalDateTime.now());
            } else if (status == 4) {
                order.setFinishTime(LocalDateTime.now());
            }
            
            orderMapper.updateById(order);
        }
    }

    /**
     * 取消订单
     */
    public void cancelOrder(Long id) {
        updateStatus(id, 5);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 10000));
        return "ORDER" + timestamp + String.format("%04d", Integer.parseInt(random));
    }

    /**
     * 获取订单统计数据（管理后台）
     */
    public Map<String, Object> getStatistics() {
        // 待付款订单数
        LambdaQueryWrapper<Order> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Order::getStatus, 1);
        Long pendingCount = orderMapper.selectCount(pendingWrapper);

        // 待发货订单数
        LambdaQueryWrapper<Order> toShipWrapper = new LambdaQueryWrapper<>();
        toShipWrapper.eq(Order::getStatus, 2);
        Long toShipCount = orderMapper.selectCount(toShipWrapper);

        // 已发货订单数
        LambdaQueryWrapper<Order> shippedWrapper = new LambdaQueryWrapper<>();
        shippedWrapper.eq(Order::getStatus, 3);
        Long shippedCount = orderMapper.selectCount(shippedWrapper);

        // 已完成订单数
        LambdaQueryWrapper<Order> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Order::getStatus, 4);
        Long completedCount = orderMapper.selectCount(completedWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("pending", pendingCount);
        result.put("toShip", toShipCount);
        result.put("shipped", shippedCount);
        result.put("completed", completedCount);
        return result;
    }

    /**
     * 获取用户订单统计数据（小程序端）
     */
    public Map<String, Long> getStatistics(Long userId) {
        LambdaQueryWrapper<Order> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(Order::getUserId, userId);
        
        long unpaidCount = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId).eq(Order::getStatus, 1));
        long unshippedCount = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId).eq(Order::getStatus, 2));
        long shippedCount = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId).eq(Order::getStatus, 3));
        long completedCount = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId).eq(Order::getStatus, 4));
        
        Map<String, Long> result = new HashMap<>();
        result.put("unpaid", unpaidCount);
        result.put("unshipped", unshippedCount);
        result.put("shipped", shippedCount);
        result.put("completed", completedCount);
        
        return result;
    }
}

