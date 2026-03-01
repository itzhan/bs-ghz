package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.ReviewDTO;
import com.campus.dining.entity.OrderInfo;
import com.campus.dining.entity.Review;
import com.campus.dining.entity.Stall;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.OrderInfoMapper;
import com.campus.dining.mapper.ReviewMapper;
import com.campus.dining.mapper.StallMapper;
import com.campus.dining.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final StallMapper stallMapper;

    @Override
    public Review createReview(Long userId, ReviewDTO dto) {
        OrderInfo order = orderInfoMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        Review review = new Review();
        review.setOrderId(dto.getOrderId());
        review.setUserId(userId);
        review.setStallId(order.getStallId());
        review.setDishId(dto.getDishId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setStatus(0); // 正常
        reviewMapper.insert(review);

        // 更新档口平均评分
        updateStallAvgRating(order.getStallId());

        return review;
    }

    @Override
    public PageResult<Review> listReviewsByStall(Long stallId, int page, int size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStallId, stallId)
                .eq(Review::getStatus, 0)
                .orderByDesc(Review::getCreatedAt);

        Page<Review> pageResult = reviewMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public PageResult<Review> listReviewsByDish(Long dishId, int page, int size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getDishId, dishId)
                .eq(Review::getStatus, 0)
                .orderByDesc(Review::getCreatedAt);

        Page<Review> pageResult = reviewMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public PageResult<Review> listReviewsByUser(Long userId, int page, int size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId)
                .orderByDesc(Review::getCreatedAt);

        Page<Review> pageResult = reviewMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public void replyReview(Long reviewId, String replyContent) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setReplyContent(replyContent);
        review.setReplyAt(LocalDateTime.now());
        reviewMapper.updateById(review);
    }

    @Override
    public void updateReviewStatus(Long id, Integer status) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setStatus(status);
        reviewMapper.updateById(review);
    }

    @Override
    public PageResult<Review> listAllReviews(int page, int size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Review::getCreatedAt);

        Page<Review> pageResult = reviewMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    /**
     * 更新档口平均评分
     */
    private void updateStallAvgRating(Long stallId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getStallId, stallId)
                        .eq(Review::getStatus, 0)
        );
        if (reviews.isEmpty()) {
            return;
        }
        BigDecimal total = reviews.stream()
                .map(r -> BigDecimal.valueOf(r.getRating()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avg = total.divide(BigDecimal.valueOf(reviews.size()), 1, RoundingMode.HALF_UP);

        Stall stall = stallMapper.selectById(stallId);
        if (stall != null) {
            stall.setAvgRating(avg);
            stallMapper.updateById(stall);
        }
    }
}
