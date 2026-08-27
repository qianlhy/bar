package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.Cart;
import com.flowerstore.entity.Order;
import com.flowerstore.entity.OrderItem;
import com.flowerstore.entity.Product;
import com.flowerstore.mapper.CartMapper;
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

    @Autowired
    private ProductSpecService productSpecService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private PointsService pointsService;

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
            orderMap.put("pointsUsed", order.getPointsUsed());
            orderMap.put("pointsAmount", order.getPointsAmount());
            orderMap.put("coinsUsed", order.getCoinsUsed());
            orderMap.put("coinsAmount", order.getCoinsAmount());
            orderMap.put("wechatAmount", order.getWechatAmount());
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
     * @param usePoints 用户希望使用的积分数（可为 null）
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order, List<Map<String, Object>> items, Integer usePoints) {
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(1); // 待付款

        // 计算订单金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Integer count = Integer.valueOf(item.get("count").toString());
            if (count <= 0) {
                throw new RuntimeException("商品数量必须大于0");
            }
            Product product = productService.getById(Long.valueOf(item.get("productId").toString()));
            String specText = item.get("specText") == null ? "" : item.get("specText").toString();
            BigDecimal price = productSpecService.resolvePrice(product, specText);
            totalPrice = totalPrice.add(price.multiply(new BigDecimal(count)));
        }
        
        order.setTotalPrice(totalPrice);
        
        // 酒吧场景：店内消费不计运费
        BigDecimal freight = BigDecimal.ZERO;
        order.setFreight(freight);

        BigDecimal payable = totalPrice.add(freight);

        // 先落库拿订单ID，再扣积分（失败则整单回滚）
        order.setPointsUsed(0);
        order.setPointsAmount(BigDecimal.ZERO);
        order.setActualPayment(payable);
        orderMapper.insert(order);

        if (usePoints != null && usePoints > 0) {
            Map<String, Object> consumed = pointsService.consumeForOrder(
                    order.getUserId(), payable, usePoints, order.getId());
            int pointsUsed = (Integer) consumed.get("pointsUsed");
            BigDecimal pointsAmount = (BigDecimal) consumed.get("pointsAmount");
            order.setPointsUsed(pointsUsed);
            order.setPointsAmount(pointsAmount);
            order.setActualPayment(payable.subtract(pointsAmount).max(BigDecimal.ZERO));
            orderMapper.updateById(order);
        }

        // 理论上积分最多抵50%，这里仍兼容活动赠送等产生的0元订单
        if (order.getActualPayment().compareTo(BigDecimal.ZERO) <= 0) {
            order.setStatus(2);
            order.setPayTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }

        // 保存订单明细
        for (Map<String, Object> item : items) {
            OrderItem orderItem = new OrderItem();
            Product product = productService.getById(Long.valueOf(item.get("productId").toString()));
            String specText = item.get("specText") == null ? "" : item.get("specText").toString();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage() == null ? "" : product.getImage());
            orderItem.setSpecText(productSpecService.normalize(specText));
            orderItem.setPrice(productSpecService.resolvePrice(product, specText));
            orderItem.setCount(Integer.valueOf(item.get("count").toString()));
            orderItem.setSubtotal(orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount())));
            orderItemMapper.insert(orderItem);

            // 更新商品库存和销量
            productService.updateInventory(orderItem.getProductId(), orderItem.getCount());

            // 仅清理本次从购物车结算且属于当前用户的条目
            if (item.get("cartId") != null) {
                LambdaQueryWrapper<Cart> cartWrapper = new LambdaQueryWrapper<>();
                cartWrapper.eq(Cart::getId, Long.valueOf(item.get("cartId").toString()));
                cartWrapper.eq(Cart::getUserId, order.getUserId());
                cartMapper.delete(cartWrapper);
            }
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
     * 取消订单（退回已扣积分）
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return;
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                pointsService.refundForOrder(order.getUserId(), order.getPointsUsed(), order.getId());
                order.setPointsUsed(0);
                order.setPointsAmount(BigDecimal.ZERO);
            }
            // 未支付取消：清除混合支付意图（币尚未扣）
            order.setCoinsUsed(0);
            order.setCoinsAmount(BigDecimal.ZERO);
            order.setWechatAmount(null);
        }
        order.setStatus(5);
        orderMapper.updateById(order);
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

