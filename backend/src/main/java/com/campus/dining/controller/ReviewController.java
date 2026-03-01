package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.dto.ReviewDTO;
import com.campus.dining.entity.Review;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 创建评价
     */
    @PostMapping
    public Result<Review> createReview(@Valid @RequestBody ReviewDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(reviewService.createReview(userId, dto));
    }

    /**
     * 查询评价列表（公开，按档口/菜品/用户）
     */
    @GetMapping
    public Result<PageResult<Review>> listReviews(
            @RequestParam(required = false) Long stallId,
            @RequestParam(required = false) Long dishId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (stallId != null) {
            return Result.success(reviewService.listReviewsByStall(stallId, page, size));
        } else if (dishId != null) {
            return Result.success(reviewService.listReviewsByDish(dishId, page, size));
        } else if (userId != null) {
            return Result.success(reviewService.listReviewsByUser(userId, page, size));
        }
        return Result.success(reviewService.listReviewsByStall(0L, page, size));
    }

    /**
     * 查询当前用户的评价
     */
    @GetMapping("/mine")
    public Result<PageResult<Review>> listMyReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(reviewService.listReviewsByUser(userId, page, size));
    }
}
