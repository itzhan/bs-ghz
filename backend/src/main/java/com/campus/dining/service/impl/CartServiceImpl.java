package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.dining.dto.CartItemDTO;
import com.campus.dining.entity.CartItem;
import com.campus.dining.entity.Dish;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.CartItemMapper;
import com.campus.dining.mapper.DishMapper;
import com.campus.dining.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final DishMapper dishMapper;

    @Override
    public CartItem addToCart(Long userId, CartItemDTO dto) {
        Dish dish = dishMapper.selectById(dto.getDishId());
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }

        // 检查是否已存在
        CartItem existing = cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getDishId, dto.getDishId())
        );
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            cartItemMapper.updateById(existing);
            return existing;
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setDishId(dto.getDishId());
        cartItem.setStallId(dish.getStallId());
        cartItem.setQuantity(dto.getQuantity());
        cartItemMapper.insert(cartItem);
        return cartItem;
    }

    @Override
    public void updateQuantity(Long id, Integer quantity) {
        CartItem cartItem = cartItemMapper.selectById(id);
        if (cartItem == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (quantity <= 0) {
            cartItemMapper.deleteById(id);
            return;
        }
        cartItem.setQuantity(quantity);
        cartItemMapper.updateById(cartItem);
    }

    @Override
    public void removeFromCart(Long id) {
        cartItemMapper.deleteById(id);
    }

    @Override
    public void clearCart(Long userId, Long stallId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        if (stallId != null) {
            wrapper.eq(CartItem::getStallId, stallId);
        }
        cartItemMapper.delete(wrapper);
    }

    @Override
    public List<CartItem> listCartItems(Long userId, Long stallId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        if (stallId != null) {
            wrapper.eq(CartItem::getStallId, stallId);
        }
        wrapper.orderByDesc(CartItem::getCreatedAt);
        return cartItemMapper.selectList(wrapper);
    }
}
