package com.flowerstore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.common.PageResult;
import com.flowerstore.common.Result;
import com.flowerstore.entity.Order;
import com.flowerstore.entity.OrderItem;
import com.flowerstore.service.OrderService;
import com.flowerstore.util.JwtUtils;
import com.flowerstore.websocket.OrderNotificationWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 分页查询订单列表（管理端）
     */
    @GetMapping("/page")
    public Result<PageResult<Order>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId) {
        Page<Order> page = orderService.getPage(current, size, orderNo, status, userId);
        PageResult<Order> result = new PageResult<>(
                page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
        return Result.success(result);
    }

    /**
     * 根据用户ID查询订单列表（小程序端）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getList(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer status) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            List<Map<String, Object>> list = orderService.getListByUserId(userId, status);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单统计（小程序端）
     */
    @GetMapping("/statistics")
    public Result<Map<String, Long>> getStatistics(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            Map<String, Long> statistics = orderService.getStatistics(userId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        List<OrderItem> items = orderService.getOrderItems(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        
        return Result.success(result);
    }

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Order> create(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            Order order = new Order();
            order.setUserId(userId);
            order.setReceiverName(params.get("receiverName").toString());
            order.setReceiverPhone(params.get("receiverPhone").toString());
            order.setProvince(params.get("province").toString());
            order.setCity(params.get("city").toString());
            order.setDistrict(params.get("district").toString());
            order.setAddress(params.get("address").toString());
            order.setPaymentMethod(params.get("paymentMethod").toString());
            order.setRemark(params.get("remark") != null ? params.get("remark").toString() : "");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
            
            Order newOrder = orderService.createOrder(order, items);
            
            // 发送新订单通知
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("orderId", newOrder.getId());
            notificationData.put("orderNo", newOrder.getOrderNo());
            notificationData.put("actualPayment", newOrder.getActualPayment());
            OrderNotificationWebSocket.sendNewOrderNotification(notificationData);
            
            return Result.success(newOrder);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            orderService.updateStatus(id, status);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/admin/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = orderService.getStatistics();
        return Result.success(statistics);
    }
}

