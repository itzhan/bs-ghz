package com.campus.dining.controller;

import com.campus.dining.common.Result;
import com.campus.dining.dto.CartItemDTO;
import com.campus.dining.entity.CartItem;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 查询购物车列表
     */
    @GetMapping
    public Result<List<CartItem>> listCartItems(
            @RequestParam(required = false) Long stallId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(cartService.listCartItems(userId, stallId));
    }

    /**
     * 添加购物车
     */
    @PostMapping
    public Result<CartItem> addToCart(@Valid @RequestBody CartItemDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(cartService.addToCart(userId, dto));
    }

    /**
     * 修改购物车商品数量
     */
    @PutMapping("/{id}")
    public Result<?> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer quantity = body.get("quantity");
        cartService.updateQuantity(id, quantity);
        return Result.success();
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{id}")
    public Result<?> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return Result.success();
    }

    /**
     * 清空购物车（按档口）
     */
    @DeleteMapping("/clear")
    public Result<?> clearCart(@RequestParam Long stallId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        cartService.clearCart(userId, stallId);
        return Result.success();
    }
}
