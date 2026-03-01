package com.campus.dining.service;

import com.campus.dining.dto.CartItemDTO;
import com.campus.dining.entity.CartItem;

import java.util.List;

public interface CartService {

    /**
     * 添加购物车（已有则累加数量）
     */
    CartItem addToCart(Long userId, CartItemDTO dto);

    /**
     * 更新购物车数量
     */
    void updateQuantity(Long id, Integer quantity);

    /**
     * 移除购物车项
     */
    void removeFromCart(Long id);

    /**
     * 清空指定档口购物车
     */
    void clearCart(Long userId, Long stallId);

    /**
     * 获取购物车列表
     */
    List<CartItem> listCartItems(Long userId, Long stallId);
}
